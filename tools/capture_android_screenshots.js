const fs = require("fs");
const path = require("path");
const { execFileSync } = require("child_process");

const root = path.resolve(__dirname, "..");
const adb = "C:\\Program Files (x86)\\Android\\android-sdk\\platform-tools\\adb.exe";
const outDir = path.join(root, "screenshots", "malayan_quest_android_exact");
fs.mkdirSync(outDir, { recursive: true });

function run(args, options = {}) {
  return execFileSync(adb, args, { encoding: options.encoding || "utf8", stdio: options.stdio || "pipe" });
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

function screenshot(name) {
  sleep(450);
  const png = execFileSync(adb, ["exec-out", "screencap", "-p"], { encoding: "buffer" });
  fs.writeFileSync(path.join(outDir, name), png);
  console.log(`captured ${name}`);
}

function dumpXml() {
  run(["shell", "uiautomator", "dump", "/sdcard/window.xml"]);
  const xml = run(["shell", "cat", "/sdcard/window.xml"]);
  return xml;
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
  const xml = dumpXml();
  const result = [];
  const regex = /<node\b[^>]*>/g;
  let match;
  while ((match = regex.exec(xml))) {
    const tag = match[0];
    const text = decodeXml((tag.match(/\btext="([^"]*)"/) || [])[1] || "");
    const desc = decodeXml((tag.match(/\bcontent-desc="([^"]*)"/) || [])[1] || "");
    const bounds = (tag.match(/\bbounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"/) || []).slice(1).map(Number);
    if (bounds.length === 4) result.push({ text, desc, bounds });
  }
  return result;
}

function center(bounds) {
  return [Math.round((bounds[0] + bounds[2]) / 2), Math.round((bounds[1] + bounds[3]) / 2)];
}

function findNode(label, mode = "exact") {
  const lower = label.toLowerCase();
  const candidates = nodes().filter((node) => {
    const values = [node.text, node.desc].filter(Boolean);
    return values.some((value) => {
      const normalized = value.toLowerCase();
      if (mode === "contains") return normalized.includes(lower);
      return normalized === lower;
    });
  });
  if (!candidates.length) {
    throw new Error(`Could not find UI text: ${label}`);
  }
  candidates.sort((a, b) => {
    const areaA = (a.bounds[2] - a.bounds[0]) * (a.bounds[3] - a.bounds[1]);
    const areaB = (b.bounds[2] - b.bounds[0]) * (b.bounds[3] - b.bounds[1]);
    return areaA - areaB;
  });
  return candidates[0];
}

function waitForText(label, mode = "exact", timeoutMs = 6000) {
  const start = Date.now();
  while (Date.now() - start < timeoutMs) {
    try {
      findNode(label, mode);
      return true;
    } catch (error) {
      sleep(250);
    }
  }
  findNode(label, mode);
  return false;
}

function tapText(label, mode = "exact") {
  const [x, y] = center(findNode(label, mode).bounds);
  run(["shell", "input", "tap", String(x), String(y)]);
  sleep(500);
}

function tapTextAfterScroll(label, mode = "exact", maxScrolls = 3) {
  for (let i = 0; i <= maxScrolls; i++) {
    try {
      tapText(label, mode);
      return true;
    } catch (error) {
      if (i === maxScrolls) throw error;
      scrollDown();
    }
  }
  return false;
}

function tap(x, y) {
  run(["shell", "input", "tap", String(x), String(y)]);
  sleep(350);
}

function typeText(value) {
  run(["shell", "input", "text", sanitizeAdbText(value)]);
  sleep(350);
}

function pressBack() {
  run(["shell", "input", "keyevent", "4"]);
  sleep(450);
}

function scrollDown() {
  run(["shell", "input", "swipe", "540", "1880", "540", "820", "420"]);
  sleep(500);
}

function scrollUp() {
  run(["shell", "input", "swipe", "540", "820", "540", "1880", "420"]);
  sleep(500);
}

function goHome() {
  for (let i = 0; i < 4; i++) {
    const text = dumpXml();
    if (text.includes('text="What do you want to do today?"') || text.includes('text="Post Errand"')) return;
    pressBack();
  }
}

function ensureAppOpen() {
  const text = dumpXml();
  if (!text.includes('package="com.malayanquest.app"')) {
    run(["shell", "am", "start", "-n", "com.malayanquest.app/.MainActivity"]);
    sleep(1200);
  }
}

function returnToAdminDashboard() {
  pressBack();
  ensureAppOpen();
  waitForText("Admin Tools", "exact", 8000);
}

function loginAs(email) {
  run(["shell", "pm", "clear", "com.malayanquest.app"]);
  run(["shell", "am", "start", "-n", "com.malayanquest.app/.MainActivity"]);
  sleep(180);
  screenshot("01_splash.png");
  waitForText("Login", "exact", 8000);
  screenshot("02_login.png");
  tapText("School email", "contains");
  typeText(email);
  tapText("Password");
  typeText("password");
  tapText("Login");
  sleep(1800);
}

function captureStudentScreens() {
  loginAs("juan.dcruz@mcl.edu.ph");
  screenshot("04_student_dashboard.png");

  tapText("Post Errand");
  screenshot("05_post_errand_top.png");
  scrollDown();
  screenshot("06_post_errand_bottom.png");
  pressBack();

  tapText("Browse Errands");
  sleep(1300);
  screenshot("07_browse_errands.png");
  tapTextAfterScroll("View Details");
  sleep(700);
  screenshot("08_errand_details_top.png");
  scrollDown();
  screenshot("09_errand_details_bottom.png");
  tap(540, 1945);
  screenshot("14_report_errand.png");
  pressBack();
  tapText("Browse Errands");
  sleep(1300);
  tapTextAfterScroll("View Details");
  sleep(700);
  scrollDown();
  tap(540, 1735);
  screenshot("10_apply_as_helper.png");
  pressBack();

  tapText("My Posted");
  sleep(1300);
  screenshot("11_my_posted_errands.png");
  try {
    tapTextAfterScroll("Status Tracker");
    screenshot("15_errand_status_top.png");
    scrollDown();
    screenshot("32_errand_status_bottom.png");
    pressBack();
    tapText("My Posted");
    sleep(1300);
    tapTextAfterScroll("Cancel Errand");
    screenshot("33_cancel_errand.png");
    pressBack();
    tapText("My Posted");
    sleep(1300);
  } catch (error) {
    console.warn(`skipped status/cancel capture: ${error.message}`);
  }
  let returnedToDashboard = false;
  if (dumpXml().includes('text="View Applicants"')) {
    tapText("View Applicants");
    sleep(1000);
    screenshot("12_applicants.png");
    if (dumpXml().includes('text="Helper Profile"')) {
      tapText("Helper Profile");
      screenshot("13_helper_profile_preview.png");
      pressBack();
      returnedToDashboard = true;
    }
    if (!returnedToDashboard) pressBack();
  }
  if (dumpXml().includes('text="Status Tracker"')) {
    tapText("Status Tracker");
    screenshot("14_errand_status_top.png");
    scrollDown();
    screenshot("15_errand_status_bottom.png");
    pressBack();
  }
  if (!returnedToDashboard) pressBack();
  ensureAppOpen();

  tapText("My Helper");
  sleep(1300);
  screenshot("16_my_helper_errands.png");
  let myHelperReturnedToDashboard = false;
  if (dumpXml().includes('text="Messages"')) {
    tapText("Messages");
    sleep(900);
    screenshot("17_chat.png");
    pressBack();
    myHelperReturnedToDashboard = true;
  }
  if (!myHelperReturnedToDashboard) pressBack();
  ensureAppOpen();

  tapText("Messages");
  screenshot("18_message_hub.png");
  pressBack();
  ensureAppOpen();

  tapText("History");
  sleep(1300);
  screenshot("19_history.png");
  let historyReturnedToDashboard = false;
  if (dumpXml().includes('text="Ratings"')) {
    tapText("Ratings");
    sleep(1000);
    screenshot("20_user_ratings.png");
    pressBack();
    historyReturnedToDashboard = true;
  }
  if (!historyReturnedToDashboard) pressBack();
  ensureAppOpen();

  tapText("Profile");
  sleep(1300);
  screenshot("21_profile.png");
  tapText("Edit Profile");
  screenshot("22_edit_profile.png");
  pressBack();
  ensureAppOpen();

  tapText("Report");
  screenshot("23_report_user.png");
  pressBack();
}

function captureRegistration() {
  run(["shell", "pm", "clear", "com.malayanquest.app"]);
  run(["shell", "am", "start", "-n", "com.malayanquest.app/.MainActivity"]);
  waitForText("Create student account", "exact", 8000);
  tapText("Create student account");
  screenshot("03_register.png");
}

function captureAdminScreens() {
  loginAs("admin@mcl.edu.ph");
  screenshot("24_admin_dashboard.png");
  tapText("Manage Users");
  sleep(1200);
  screenshot("25_admin_manage_users.png");
  let adminReturnedToDashboard = false;
  if (dumpXml().includes('text="View"')) {
    tapText("View");
    screenshot("26_admin_user_details.png");
    pressBack();
    adminReturnedToDashboard = true;
  }
  if (!adminReturnedToDashboard) returnToAdminDashboard();
  ensureAppOpen();

  tapText("Manage Errands");
  sleep(1200);
  screenshot("27_admin_manage_errands.png");
  returnToAdminDashboard();

  tapText("Reports");
  sleep(1200);
  screenshot("28_admin_reports.png");
  returnToAdminDashboard();

  tapText("Flagged Errands");
  sleep(1200);
  screenshot("29_admin_flagged_errands.png");
  returnToAdminDashboard();

  tapText("Ratings");
  screenshot("30_admin_ratings.png");
  returnToAdminDashboard();

  tapText("Moderation");
  sleep(1200);
  screenshot("31_admin_moderation_logs.png");
  returnToAdminDashboard();
}

captureRegistration();
captureStudentScreens();
captureAdminScreens();
