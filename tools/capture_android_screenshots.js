const fs = require("fs");
const path = require("path");
const { execFileSync } = require("child_process");

const root = path.resolve(__dirname, "..");
const adbCandidates = [
  process.env.ADB_PATH,
  process.env.LOCALAPPDATA && path.join(process.env.LOCALAPPDATA, "Android", "Sdk", "platform-tools", "adb.exe"),
  "C:\\Program Files (x86)\\Android\\android-sdk\\platform-tools\\adb.exe",
].filter(Boolean);
const adb = adbCandidates.find((candidate) => fs.existsSync(candidate));

if (!adb) {
  throw new Error("ADB was not found. Set ADB_PATH to the Android platform-tools adb executable.");
}

const serial = process.env.ANDROID_SERIAL || "emulator-5554";
const outRoot = process.env.MQ_SCREENSHOT_OUT
  ? path.resolve(process.env.MQ_SCREENSHOT_OUT)
  : path.join(root, "screenshots");
const userDir = path.join(outRoot, "interface_for_user");
const adminDir = path.join(outRoot, "interface_for_admin");

fs.mkdirSync(userDir, { recursive: true });
fs.mkdirSync(adminDir, { recursive: true });

function runAdb(args, options = {}) {
  return execFileSync(adb, ["-s", serial, ...args], {
    encoding: options.encoding || "utf8",
    stdio: options.stdio || "pipe",
  });
}

function sleep(ms) {
  Atomics.wait(new Int32Array(new SharedArrayBuffer(4)), 0, 0, ms);
}

function sanitizeAdbText(value) {
  return String(value)
    .replace(/\\/g, "\\\\")
    .replace(/\s/g, "%s")
    .replace(/&/g, "\\&")
    .replace(/</g, "\\<")
    .replace(/>/g, "\\>")
    .replace(/\(/g, "\\(")
    .replace(/\)/g, "\\)")
    .replace(/;/g, "\\;")
    .replace(/"/g, '\\"')
    .replace(/'/g, "\\'");
}

function capture(directory, name) {
  sleep(650);
  const png = execFileSync(adb, ["-s", serial, "exec-out", "screencap", "-p"], { encoding: "buffer" });
  fs.writeFileSync(path.join(directory, name), png);
  console.log(`captured ${path.basename(directory)}/${name}`);
}

function dumpXml() {
  runAdb(["shell", "uiautomator", "dump", "/sdcard/mq-window.xml"]);
  return runAdb(["shell", "cat", "/sdcard/mq-window.xml"]);
}

function decodeXml(value) {
  return value
    .replace(/&quot;/g, '"')
    .replace(/&apos;/g, "'")
    .replace(/&lt;/g, "<")
    .replace(/&gt;/g, ">")
    .replace(/&amp;/g, "&");
}

function nodes() {
  const result = [];
  const regex = /<node\b[^>]*>/g;
  let match;
  const xml = dumpXml();

  while ((match = regex.exec(xml))) {
    const tag = match[0];
    const text = decodeXml((tag.match(/\btext="([^"]*)"/) || [])[1] || "");
    const desc = decodeXml((tag.match(/\bcontent-desc="([^"]*)"/) || [])[1] || "");
    const bounds = (tag.match(/\bbounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"/) || []).slice(1).map(Number);
    if (bounds.length === 4) result.push({ text, desc, bounds });
  }
  return result;
}

function center(node) {
  const [left, top, right, bottom] = node.bounds;
  return [Math.round((left + right) / 2), Math.round((top + bottom) / 2)];
}

function matchingNodes(label, mode = "exact") {
  const expected = label.toLowerCase();
  return nodes().filter((node) => [node.text, node.desc].some((value) => {
    const current = value.toLowerCase();
    return mode === "contains" ? current.includes(expected) : current === expected;
  }));
}

function findNode(label, mode = "exact", position = "first") {
  const expected = label.toLowerCase();
  const matches = matchingNodes(label, mode).sort((a, b) => {
    const aTextMatch = mode === "contains"
      ? a.text.toLowerCase().includes(expected)
      : a.text.toLowerCase() === expected;
    const bTextMatch = mode === "contains"
      ? b.text.toLowerCase().includes(expected)
      : b.text.toLowerCase() === expected;
    if (aTextMatch !== bTextMatch) return aTextMatch ? -1 : 1;
    return center(a)[1] - center(b)[1];
  });
  if (!matches.length) throw new Error(`Could not find UI text or description: ${label}`);
  return position === "last" ? matches[matches.length - 1] : matches[0];
}

function waitFor(label, mode = "exact", timeoutMs = 8000) {
  const started = Date.now();
  while (Date.now() - started < timeoutMs) {
    try {
      return findNode(label, mode);
    } catch (_) {
      sleep(300);
    }
  }
  return findNode(label, mode);
}

function tapNode(node) {
  const [x, y] = center(node);
  runAdb(["shell", "input", "tap", String(x), String(y)]);
  sleep(650);
}

function tapText(label, mode = "exact", position = "first") {
  tapNode(findNode(label, mode, position));
}

function scrollDown() {
  runAdb(["shell", "input", "swipe", "540", "1870", "540", "690", "430"]);
  sleep(650);
}

function scrollDownSmall() {
  runAdb(["shell", "input", "swipe", "540", "1600", "540", "1030", "360"]);
  sleep(600);
}

function scrollUp() {
  runAdb(["shell", "input", "swipe", "540", "700", "540", "1870", "430"]);
  sleep(650);
}

function tapAfterScroll(label, mode = "exact", maxScrolls = 5, position = "first") {
  for (let attempt = 0; attempt <= maxScrolls; attempt++) {
    try {
      tapText(label, mode, position);
      return;
    } catch (error) {
      if (attempt === maxScrolls) throw error;
      scrollDown();
    }
  }
}

function tapVisibleTextAfterScroll(label, maxScrolls = 6) {
  for (let attempt = 0; attempt <= maxScrolls; attempt++) {
    const matches = nodes()
      .filter((node) => node.text.toLowerCase() === label.toLowerCase())
      .sort((a, b) => center(a)[1] - center(b)[1]);
    if (matches.length) {
      tapNode(matches[matches.length - 1]);
      return;
    }
    if (attempt === maxScrolls) throw new Error(`Could not find visible UI text: ${label}`);
    scrollDown();
  }
}

function tapLabelNearTitle(title, label) {
  const all = nodes();
  const titleNode = all.find((node) => node.text.toLowerCase() === title.toLowerCase());
  if (!titleNode) throw new Error(`Could not find record title: ${title}`);
  const titleY = center(titleNode)[1];
  const candidates = all
    .filter((node) => node.text.toLowerCase() === label.toLowerCase() && center(node)[1] >= titleY)
    .sort((a, b) => Math.abs(center(a)[1] - titleY) - Math.abs(center(b)[1] - titleY));
  if (!candidates.length) throw new Error(`Could not find ${label} for ${title}`);
  tapNode(candidates[0]);
}

function tapActionForTitle(title) {
  const all = nodes();
  const titleNode = all.find((node) => node.text.toLowerCase() === title.toLowerCase());
  if (!titleNode) throw new Error(`Could not find errand title: ${title}`);
  const titleY = center(titleNode)[1];
  const actions = all
    .filter((node) => node.desc === "Actions")
    .sort((a, b) => Math.abs(center(a)[1] - titleY) - Math.abs(center(b)[1] - titleY));
  if (!actions.length) throw new Error(`Could not find actions menu for ${title}`);
  tapNode(actions[0]);
}

function pressBack() {
  runAdb(["shell", "input", "keyevent", "4"]);
  sleep(700);
}

function typeText(value) {
  runAdb(["shell", "input", "text", sanitizeAdbText(value)]);
  sleep(450);
}

function startClean() {
  runAdb(["shell", "pm", "clear", "com.malayanquest.app"]);
  runAdb(["shell", "am", "start", "-n", "com.malayanquest.app/.MainActivity"]);
}

function loginAs(email) {
  startClean();
  // pm clear can briefly leave the previous activity surface visible. Wait for
  // the newly started process to finish its splash transition before typing.
  sleep(2500);
  waitFor("School email", "contains", 9000);
  tapText("School email", "exact");
  typeText(email);
  waitFor(email);
  tapText("Password");
  typeText("password");
  runAdb(["shell", "input", "keyevent", "4"]);
  sleep(500);
  tapText("Login", "exact", "last");
  sleep(1800);
}

function captureAuthInterfaces() {
  startClean();
  sleep(120);
  capture(userDir, "01_splash.png");
  waitFor("School email", "contains", 9000);
  capture(userDir, "02_login.png");
  tapText("Create student account");
  waitFor("Full name", "contains");
  capture(userDir, "03_register.png");
}

function captureFeedAndPosting() {
  loginAs("juan.dcruz@mcl.edu.ph");
  waitFor("Feed");
  capture(userDir, "04_errand_feed.png");
  tapText("Post Errand");
  waitFor("Campus-only request");
  capture(userDir, "05_post_errand_top.png");
  scrollDown();
  scrollDown();
  capture(userDir, "06_post_errand_bottom.png");
}

function captureBrowseDetailsApplyAndReport() {
  loginAs("juan.dcruz@mcl.edu.ph");
  waitFor("Apply as Helper");
  tapText("Actions");
  tapText("View Details");
  waitFor("Safety Note");
  capture(userDir, "07_errand_details_top.png");
  scrollDown();
  capture(userDir, "08_errand_details_bottom.png");
  tapAfterScroll("Apply as Helper");
  waitFor("Your helper profile shown to requester");
  capture(userDir, "09_apply_as_helper_top.png");
  scrollDown();
  waitFor("Helper Offer");
  capture(userDir, "10_apply_as_helper_bottom.png");
  pressBack();
  waitFor("Errand Feed");
  tapText("Actions");
  tapText("View Details");
  waitFor("Safety Note");
  tapAfterScroll("Report Errand");
  waitFor("Report Details");
  capture(userDir, "11_report_errand.png");
}

function captureMyTasksWorkflow() {
  loginAs("juan.dcruz@mcl.edu.ph");
  tapText("My Tasks");
  waitFor("Requested");
  capture(userDir, "12_my_tasks_requested.png");

  tapAfterScroll("View Applicants");
  waitFor("Helper Profile");
  capture(userDir, "13_applicants.png");
  tapText("Helper Profile");
  waitFor("Public helper card");
  capture(userDir, "14_helper_profile.png");
  tapVisibleTextAfterScroll("Back");
  waitFor("Helper Profile");
  tapVisibleTextAfterScroll("Back");

  waitFor("Requested");
  tapActionForTitle("Buy bluebook from bookstore");
  tapText("Status Tracker");
  waitFor("Errand Status");
  capture(userDir, "15_status_tracker_top.png");
  scrollDown();
  waitFor("Current status");
  capture(userDir, "16_status_tracker_bottom.png");
  tapVisibleTextAfterScroll("Back");
  waitFor("Feed");
  tapText("My Tasks");

  waitFor("Requested");
  tapActionForTitle("Buy bluebook from bookstore");
  tapText("Cancel Errand");
  waitFor("Cancel carefully");
  capture(userDir, "17_cancel_errand.png");
  tapVisibleTextAfterScroll("Back");
  waitFor("Feed");
  tapText("My Tasks");

  waitFor("Requested");
  tapAfterScroll("Confirm Completion");
  waitFor("Completion confirmation");
  capture(userDir, "18_completion_confirmation.png");
  tapVisibleTextAfterScroll("Back");

  waitFor("Requested");
  tapAfterScroll("Open Chat");
  waitFor("Message");
  capture(userDir, "19_chat.png");
  pressBack();
  waitFor("Feed");
  tapText("My Tasks");

  waitFor("Requested");
  tapText("Doing");
  sleep(1200);
  capture(userDir, "20_my_tasks_doing.png");
}

function captureProfileHistoryAndRating() {
  loginAs("juan.dcruz@mcl.edu.ph");
  tapText("Profile");
  waitFor("Settings & Tools");
  capture(userDir, "21_profile.png");
  tapText("Edit Profile");
  waitFor("Display Information");
  capture(userDir, "22_edit_profile.png");
  tapVisibleTextAfterScroll("Back");

  waitFor("History");
  tapText("History");
  waitFor("All");
  capture(userDir, "23_history.png");
  tapAfterScroll("Ratings", "exact", 12);
  sleep(1200);
  capture(userDir, "24_user_ratings.png");
  tapVisibleTextAfterScroll("Back");

  waitFor("All");
  for (let i = 0; i < 10; i++) scrollUp();
  for (let i = 0; i < 20; i++) {
    try {
      tapLabelNearTitle("Buy graphing paper", "View Details");
      break;
    } catch (error) {
      if (i === 19) throw error;
      scrollDownSmall();
    }
  }
  waitFor("Status Tracker");
  tapAfterScroll("Rate Helper");
  waitFor("Rating 1-5", "contains");
  capture(userDir, "25_rate_helper.png");
  pressBack();

  tapText("Profile");
  waitFor("Settings & Tools");
  tapAfterScroll("Report a Problem");
  waitFor("Find Errand");
  capture(userDir, "26_report_problem.png");
}

function returnToAdminDashboard() {
  pressBack();
  waitFor("Admin Tools", "exact", 9000);
}

function openAdminTool(label) {
  tapAfterScroll(label, "exact", 4);
  sleep(1200);
}

function captureAdminInterfaces() {
  loginAs("admin@mcl.edu.ph");
  waitFor("Activity Overview");
  capture(adminDir, "01_admin_dashboard_overview.png");
  scrollDown();
  capture(adminDir, "02_admin_dashboard_tools.png");

  openAdminTool("Manage Users");
  waitFor("Search name", "contains");
  capture(adminDir, "03_manage_users.png");
  tapAfterScroll("View");
  waitFor("Admin Actions");
  capture(adminDir, "04_user_details.png");
  returnToAdminDashboard();

  openAdminTool("Manage Errands");
  waitFor("Open");
  capture(adminDir, "05_manage_errands.png");
  tapAfterScroll("View");
  waitFor("Remove Errand");
  capture(adminDir, "06_errand_details.png");
  returnToAdminDashboard();

  openAdminTool("Reports");
  waitFor("Pending");
  capture(adminDir, "07_reports.png");
  tapAfterScroll("View");
  waitFor("Resolve Report");
  capture(adminDir, "08_report_details.png");
  returnToAdminDashboard();

  openAdminTool("Flagged Errands");
  waitFor("Flagged");
  capture(adminDir, "09_flagged_errands.png");
  tapAfterScroll("View");
  waitFor("Remove Errand");
  capture(adminDir, "10_flagged_errand_details.png");
  returnToAdminDashboard();

  openAdminTool("Ratings");
  waitFor("Search user, errand, or feedback", "contains");
  capture(adminDir, "11_ratings_and_feedback.png");
}

const section = process.env.MQ_CAPTURE_SECTION || "all";
const sections = {
  auth: captureAuthInterfaces,
  feed: captureFeedAndPosting,
  browse: captureBrowseDetailsApplyAndReport,
  tasks: captureMyTasksWorkflow,
  profile: captureProfileHistoryAndRating,
  admin: captureAdminInterfaces,
};

if (section === "all") {
  Object.values(sections).forEach((captureSection) => captureSection());
} else if (sections[section]) {
  sections[section]();
} else {
  throw new Error(`Unknown MQ_CAPTURE_SECTION: ${section}`);
}

console.log(`Finished current-interface capture in ${outRoot}`);
