const fs = require("fs");
const path = require("path");
const { chromium } = require("playwright");

const root = path.resolve(__dirname, "..");
const outDir = path.join(root, "screenshots", "malayan_quest_interfaces");
fs.mkdirSync(outDir, { recursive: true });

const css = `
  :root {
    --primary: #0B4EA2;
    --secondary: #1976D2;
    --bg: #EEF5FB;
    --card: #FFFFFF;
    --light: #F4F8FC;
    --border: #D7E6F5;
    --text: #102A43;
    --muted: #6B7280;
    --success: #2E7D32;
    --warning: #F9A825;
    --danger: #D32F2F;
  }
  * { box-sizing: border-box; }
  body {
    margin: 0;
    padding: 24px;
    background: #dbe8f5;
    font-family: Arial, Helvetica, sans-serif;
    color: var(--text);
  }
  .phone {
    width: 390px;
    min-height: 844px;
    margin: 0 auto;
    background: var(--bg);
    overflow: hidden;
    box-shadow: 0 18px 42px rgba(16,42,67,.18);
    border-radius: 26px;
    border: 1px solid rgba(11,78,162,.14);
  }
  .phone.auth { background: #fff; }
  .header {
    background: #fff;
    border-bottom: 1px solid var(--border);
    padding: 18px 16px 14px;
    display: grid;
    grid-template-columns: 40px 1fr 40px;
    align-items: center;
    text-align: center;
  }
  .back {
    width: 40px;
    height: 40px;
    border-radius: 20px;
    display: grid;
    place-items: center;
    color: var(--primary);
    font-size: 24px;
    font-weight: 700;
  }
  .header h1 {
    margin: 0;
    font-size: 16px;
    line-height: 1.2;
  }
  .header p {
    margin: 4px 0 0;
    color: var(--muted);
    font-size: 11px;
  }
  .dashHeader {
    background: var(--primary);
    color: #fff;
    padding: 20px 20px 22px;
  }
  .dashHeader .top {
    display: flex;
    align-items: center;
    justify-content: space-between;
    color: #dceafa;
    font-size: 12px;
    font-weight: 700;
    margin-bottom: 12px;
  }
  .dashHeader h1 {
    margin: 0;
    font-size: 24px;
    line-height: 1.15;
  }
  .dashHeader p {
    color: #dceafa;
    margin: 7px 0 10px;
    font-size: 13px;
  }
  .content {
    padding: 14px 16px;
    display: flex;
    flex-direction: column;
    gap: 12px;
  }
  .auth .content {
    padding: 28px 24px;
  }
  .authTitle {
    text-align: center;
    padding: 22px 0 10px;
  }
  .authTitle h1 {
    margin: 0 0 6px;
    font-size: 22px;
  }
  .authTitle p {
    margin: 0;
    color: var(--muted);
    font-size: 13px;
  }
  .splash {
    align-items: center;
    text-align: center;
    padding-top: 74px;
  }
  .logo {
    width: 104px;
    height: 104px;
    border-radius: 52px;
    display: grid;
    place-items: center;
    background: #eaf3ff;
    color: var(--primary);
    font-size: 38px;
    font-weight: 800;
  }
  .splash h1 {
    margin: 0;
    color: var(--primary);
    font-size: 28px;
  }
  .splash p {
    margin: 0;
    color: var(--secondary);
    font-size: 15px;
  }
  .card {
    background: var(--card);
    border-radius: 10px;
    padding: 14px;
    box-shadow: 0 1px 4px rgba(16,42,67,.08);
    display: flex;
    flex-direction: column;
    gap: 8px;
  }
  .card.light { background: var(--light); }
  .section {
    color: var(--primary);
    font-size: 18px;
    font-weight: 800;
    margin: 2px 0;
  }
  .textMuted {
    color: var(--muted);
    font-size: 14px;
    line-height: 1.35;
    white-space: pre-line;
  }
  .field {
    min-height: 56px;
    border: 1px solid #b7c7d8;
    border-radius: 8px;
    background: #fff;
    padding: 8px 12px;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
  .field.multi { min-height: 92px; justify-content: flex-start; padding-top: 10px; }
  .field label {
    color: var(--muted);
    font-size: 12px;
    margin-bottom: 5px;
  }
  .field .value {
    color: var(--text);
    font-size: 14px;
  }
  .button {
    height: 50px;
    border-radius: 8px;
    background: var(--primary);
    color: #fff;
    display: grid;
    place-items: center;
    font-weight: 800;
    font-size: 14px;
    border: 1px solid var(--primary);
  }
  .button.secondary {
    background: #fff;
    color: var(--primary);
    border-color: #8aa8c9;
  }
  .button.danger {
    background: var(--danger);
    border-color: var(--danger);
  }
  .centerText {
    text-align: center;
    color: var(--muted);
    font-size: 12px;
  }
  .badge {
    display: inline-flex;
    align-items: center;
    align-self: flex-start;
    padding: 5px 11px;
    border-radius: 18px;
    font-size: 12px;
    font-weight: 800;
    color: var(--primary);
    background: var(--light);
    border: 1px solid var(--primary);
    line-height: 1.1;
  }
  .badge.success { color: var(--success); border-color: var(--success); }
  .badge.warning { color: var(--warning); border-color: var(--warning); }
  .badge.danger { color: var(--danger); border-color: var(--danger); }
  .badge.white { color: #fff; background: #0A63D8; border-color: #fff; }
  .row {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  .row.between { justify-content: space-between; }
  .grow { flex: 1; }
  .cardTitle {
    font-size: 18px;
    font-weight: 800;
    margin: 0;
    line-height: 1.25;
  }
  .detail label {
    display: block;
    color: var(--secondary);
    font-size: 11px;
    font-weight: 800;
    text-transform: uppercase;
    margin-bottom: 3px;
  }
  .detail div {
    font-size: 14px;
    color: var(--text);
    line-height: 1.25;
  }
  .grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 10px;
  }
  .tile {
    height: 104px;
    border: 1px solid var(--border);
    border-radius: 8px;
    background: #fff;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 12px;
    text-align: center;
  }
  .tile .icon {
    color: var(--primary);
    font-size: 26px;
    line-height: 1;
    margin-bottom: 6px;
  }
  .tile strong {
    font-size: 12px;
    line-height: 1.2;
  }
  .tile span {
    color: var(--muted);
    font-size: 10px;
    line-height: 1.2;
    margin-top: 3px;
  }
  .tabs {
    background: #fff;
    border: 1px solid var(--border);
    border-radius: 8px;
    padding: 4px;
    display: flex;
    gap: 4px;
  }
  .tab {
    flex: 1;
    text-align: center;
    padding: 9px 4px;
    border-radius: 6px;
    color: var(--muted);
    font-size: 12px;
  }
  .tab.active {
    background: #eaf3ff;
    color: var(--primary);
    font-weight: 800;
  }
  .bottomNav {
    margin-top: 8px;
    border-top: 1px solid var(--border);
    background: #fff;
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    padding: 7px 12px;
  }
  .navItem {
    text-align: center;
    color: var(--muted);
    font-size: 10px;
    padding: 4px 0;
  }
  .navItem div {
    font-size: 20px;
    line-height: 1;
    margin-bottom: 3px;
  }
  .navItem.active {
    color: var(--primary);
    font-weight: 800;
  }
  .profileHero {
    background: var(--primary);
    color: #fff;
    border-radius: 12px;
    padding: 18px;
    text-align: center;
    display: flex;
    flex-direction: column;
    gap: 10px;
    align-items: center;
  }
  .avatar {
    width: 76px;
    height: 76px;
    border-radius: 38px;
    background: #fff;
    display: grid;
    place-items: center;
    color: var(--muted);
    font-size: 52px;
  }
  .stats {
    display: grid;
    width: 100%;
    grid-template-columns: repeat(3, 1fr);
    gap: 8px;
  }
  .stat {
    background: #fff;
    color: var(--primary);
    border-radius: 8px;
    padding: 10px 6px;
    font-weight: 800;
    font-size: 14px;
  }
  .stat span {
    display: block;
    color: var(--muted);
    font-weight: 400;
    font-size: 10px;
    margin-top: 4px;
  }
  .chat {
    max-width: 280px;
    border-radius: 14px;
    padding: 8px 12px;
    border: 1px solid var(--border);
    background: #fff;
    font-size: 14px;
    line-height: 1.35;
  }
  .chat.mine {
    margin-left: auto;
    background: var(--primary);
    border-color: var(--primary);
    color: #fff;
  }
  .chat strong {
    display: block;
    color: var(--muted);
    font-size: 12px;
    margin-bottom: 3px;
  }
  .chat.mine strong { color: #fff; }
  .tracker {
    display: flex;
    flex-direction: column;
    gap: 0;
  }
  .step {
    display: grid;
    grid-template-columns: 24px 1fr;
    gap: 12px;
    min-height: 42px;
  }
  .dotWrap { display: flex; flex-direction: column; align-items: center; }
  .dot {
    width: 20px;
    height: 20px;
    border-radius: 10px;
    border: 2px solid var(--muted);
    background: #fff;
    color: #fff;
    display: grid;
    place-items: center;
    font-size: 11px;
    font-weight: 800;
  }
  .line {
    width: 2px;
    flex: 1;
    background: var(--border);
    min-height: 22px;
  }
  .step.done .dot, .step.done .line { background: var(--success); border-color: var(--success); }
  .step.current .dot { background: var(--primary); border-color: var(--primary); }
  .step strong {
    color: var(--muted);
    font-size: 13px;
  }
  .step.done strong { color: var(--success); }
  .step.current strong { color: var(--primary); }
  .step span {
    display: block;
    color: var(--muted);
    font-size: 11px;
    margin-top: 3px;
  }
  pre.record {
    margin: 0;
    white-space: pre-wrap;
    color: var(--muted);
    font-family: Arial, Helvetica, sans-serif;
    font-size: 14px;
    line-height: 1.35;
  }
`;

const statusSteps = [
  "Open",
  "Has Applicants",
  "Assigned",
  "Accepted",
  "In Progress",
  "Completed by Helper",
  "Confirmed by Requester",
  "Rated",
  "Closed",
];

const errand = {
  title: "Pick up printed thesis draft",
  status: "Has Applicants",
  category: "Printing Pickup",
  pickup: "Printing Services",
  dropoff: "Room A305",
  deadline: "2026-07-15 13:00:00",
  reward: "PHP 60 - includes printing fee reimbursement",
  requester: "Maria Santos",
  rating: "4.80",
};

function htmlEscape(value) {
  return String(value)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
}

function badge(text, tone = "") {
  return `<span class="badge ${tone}">${htmlEscape(text)}</span>`;
}

function button(text, kind = "") {
  return `<div class="button ${kind}">${htmlEscape(text)}</div>`;
}

function field(label, value = "", multi = false) {
  return `<div class="field ${multi ? "multi" : ""}"><label>${htmlEscape(label)}</label><div class="value">${htmlEscape(value)}</div></div>`;
}

function card(inner, extra = "") {
  return `<div class="card ${extra}">${inner}</div>`;
}

function info(text) {
  return card(`<div class="textMuted">${htmlEscape(text)}</div>`, "light");
}

function detail(label, value) {
  return `<div class="detail"><label>${htmlEscape(label)}</label><div>${htmlEscape(value || "Not specified")}</div></div>`;
}

function authTitle(title, subtitle) {
  return `<div class="authTitle"><h1>${htmlEscape(title)}</h1><p>${htmlEscape(subtitle)}</p></div>`;
}

function tabs(items, active) {
  return `<div class="tabs">${items.map((item) => `<div class="tab ${item === active ? "active" : ""}">${htmlEscape(item)}</div>`).join("")}</div>`;
}

function actionGrid(items) {
  const icons = ["+", "?", "W", "P", "C", "H", "U", "!"];
  return `<div class="grid">${items.map((item, i) => `<div class="tile"><div class="icon">${icons[i] || ">"}</div><strong>${htmlEscape(item[0])}</strong><span>${htmlEscape(item[1])}</span></div>`).join("")}</div>`;
}

function summaryGrid(items) {
  return `<div class="grid">${items.map(([label, value]) => card(`<div style="font-size:20px;color:var(--primary);font-weight:800">${htmlEscape(value)}</div><div class="textMuted" style="font-size:13px">${htmlEscape(label)}</div>`)).join("")}</div>`;
}

function errandCore(data = errand, statusTone = "warning") {
  return `
    <div class="row between"><h2 class="cardTitle">${htmlEscape(data.title)}</h2>${badge(data.status, statusTone)}</div>
    ${badge(`[Print] ${data.category}`)}
    ${detail("Pickup", data.pickup)}
    ${detail("Drop-off", data.dropoff)}
    ${detail("Deadline", data.deadline)}
    ${detail("Reward", data.reward)}
    ${detail("Requester", data.requester)}
    ${detail("Rating", data.rating)}
  `;
}

function errandCard(applyMode = true, data = errand) {
  const actions = applyMode
    ? `${button("View Details")}${button("Apply as Helper", "secondary")}`
    : `${button("View Details")}${button("View Applicants")}${button("Status Tracker", "secondary")}${button("Cancel Errand", "secondary")}`;
  return card(`${errandCore(data)}${actions}`);
}

function applicantCard(name, status, offer, estimate) {
  return card(`
    <div class="row between"><h2 class="cardTitle">${htmlEscape(name)}</h2>${badge(status, status === "selected" ? "success" : "warning")}</div>
    ${detail("Rating", "4.90")}
    ${detail("Completed", "12 errands")}
    ${detail("Offer", offer)}
    ${detail("Estimate", estimate)}
    ${button("Helper Profile", "secondary")}
    ${status === "pending" ? button("Select Helper") : ""}
  `);
}

function adminRecord(title, lines, actions = true) {
  return card(`
    <h2 class="cardTitle">${htmlEscape(title)}</h2>
    <pre class="record">${htmlEscape(lines.join("\n"))}</pre>
    ${actions ? button("View") : ""}
  `);
}

function statusTracker(current) {
  const index = statusSteps.indexOf(current);
  return card(`
    <div class="tracker">
      ${statusSteps.map((step, i) => {
        const cls = i < index ? "done" : i === index ? "current" : "";
        return `<div class="step ${cls}">
          <div class="dotWrap"><div class="dot">${i < index ? "✓" : ""}</div>${i < statusSteps.length - 1 ? '<div class="line"></div>' : ""}</div>
          <div><strong>${htmlEscape(step)}</strong><span>${i <= index && index >= 0 ? "Updated in current workflow" : "Pending"}</span></div>
        </div>`;
      }).join("")}
    </div>
    ${info("You will be notified at every update.")}
  `);
}

function bottomNav(active = "Home") {
  const items = [["Home", "⌂"], ["Messages", "C"], ["History", "H"], ["Profile", "U"]];
  return `<div class="bottomNav">${items.map(([label, icon]) => `<div class="navItem ${label === active ? "active" : ""}"><div>${icon}</div>${label}</div>`).join("")}</div>`;
}

function shell({ title = "", subtitle = "", content, auth = false, splash = false, dashboard = false, nav = "Home" }) {
  const header = auth || splash
    ? ""
    : dashboard
      ? `<div class="dashHeader"><div class="top"><span style="font-size:24px;line-height:1">≡</span><span>Malayan Quest</span></div><h1>Hello, Juan!</h1><p>What do you want to do today?</p>${badge("Verified Prototype", "white")}</div>`
      : `<div class="header"><div class="back">‹</div><div><h1>${htmlEscape(title)}</h1><p>${htmlEscape(subtitle)}</p></div><div></div></div>`;
  return `<!doctype html><html><head><meta charset="utf-8"><style>${css}</style></head><body><main class="phone ${auth || splash ? "auth" : ""}">${header}<section class="content ${splash ? "splash" : ""}">${content}</section>${!auth && !splash && !title.startsWith("Admin") ? bottomNav(nav) : ""}</main></body></html>`;
}

const screens = [
  {
    file: "01_splash.png",
    html: shell({ splash: true, content: `<div class="logo">MQ</div><h1>Malayan Quest</h1><p>Student Errand Platform</p><div style="height:42px"></div>${badge("Prototype Version")}` }),
  },
  {
    file: "02_login.png",
    html: shell({ auth: true, content: `${authTitle("Welcome Back!", "Sign in to your account")}${field("School email", "juan.dcruz@mcl.edu.ph")}${field("Password", "••••••••")}${button("Login")}<div class="centerText">or</div>${button("Create student account", "secondary")}${info("Demo accounts use password: password\nAdmin: admin@mcl.edu.ph\nStudent: juan.dcruz@mcl.edu.ph, maria.santos@mcl.edu.ph, carlo.reyes@mcl.edu.ph")}` }),
  },
  {
    file: "03_register.png",
    html: shell({ auth: true, content: `${authTitle("Create Student Account", "All accounts are verified for prototype use.")}${info("Use your school email and student number. Student numbers and school emails are stored for verification and are not shown publicly.")}${field("Full name", "Ana Reyes")}${field("School email, example: student@mcl.edu.ph", "ana.reyes@mcl.edu.ph")}${field("Student number", "2026-00123")}${field("Password", "••••••••")}${field("Confirm password", "••••••••")}${button("Register")}${button("Back to login", "secondary")}` }),
  },
  {
    file: "04_student_dashboard.png",
    html: shell({ dashboard: true, content: `${actionGrid([["Post Errand", "Create a campus request"], ["Browse Errands", "Find helper tasks"], ["My Posted", "Applicants and updates"], ["My Helper", "Assigned work"], ["Messages", "Open active chats"], ["History", "Past errands"], ["Profile", "Ratings and account"], ["Report", "Safety concern"]])}${info("Errands must be school-related, safe, and limited to campus or nearby MCL locations.")}${button("Logout", "secondary")}` }),
  },
  {
    file: "05_post_errand.png",
    html: shell({ title: "Post Errand", subtitle: "Campus and near-campus errands only", content: `${info("Banned errands include alcohol, vape products, medicines, confidential documents, answer sheets, IDs, weapons, restricted areas, and tasks that violate school rules.")}${field("Title", "Pick up printed thesis draft")}${field("Description", "Please pick up my printed draft from the printing shop after lunch.", true)}${field("Category", "Printing Pickup")}${field("Pickup location", "Printing Services")}${field("Drop-off location", "Room A305")}${field("Deadline: 2026-07-15 13:00:00", "2026-07-15 13:00:00")}${field("Reward amount, optional", "60")}${field("Reward note, optional", "Includes printing fee reimbursement")}${button("Submit Errand")}${button("Back", "secondary")}` }),
  },
  {
    file: "06_browse_errands.png",
    html: shell({ title: "Browse Errands", subtitle: "Open errands from other students", content: `<div class="section">Errand Feed</div>${field("Search keyword or location", "printing")}${field("Category", "All Categories")}${button("Apply Filters")}${errandCard(true)}${errandCard(true, { ...errand, title: "Buy bluebook before 2 PM", status: "Open", category: "Bluebook Purchase", pickup: "Bookstore", dropoff: "Library lobby", reward: "PHP 20", requester: "Carlo Reyes", rating: "4.67" })}${button("Back", "secondary")}` }),
  },
  {
    file: "07_errand_details.png",
    html: shell({ title: "Errand Details", subtitle: errand.title, content: `${card(`${errandCore()}<div class="section">Safety Scope</div><div class="textMuted">Only accept if the request is safe, school-related, and limited to campus or nearby MCL locations.</div>`)}<div class="section">Status Tracker</div>${statusTracker("Has Applicants")}${button("Apply as Helper")}${button("Report Errand", "secondary")}${button("Back", "secondary")}` }),
  },
  {
    file: "08_apply_as_helper.png",
    html: shell({ title: "Apply as Helper", subtitle: errand.title, content: `${card(`${badge("Your helper profile shown to requester")}<h2 class="cardTitle">Juan Dela Cruz</h2><div class="textMuted">Average rating and completed errands will be loaded from your account record.</div><div class="textMuted">Offer note and estimated completion time are public to this requester only.</div>`)}${field("Offer note", "I am near the printing area after class and can bring it to A305.", true)}${field("Estimated completion time, example: 30 minutes", "25 minutes")}${button("Submit Application")}${button("Back", "secondary")}` }),
  },
  {
    file: "09_my_posted_errands.png",
    html: shell({ title: "My Posted Errands", subtitle: "Requester workflow", content: `${tabs(["Active", "Completed", "Cancelled"], "Active")}${errandCard(false)}${button("Back", "secondary")}` }),
  },
  {
    file: "10_my_helper_errands.png",
    html: shell({ title: "My Helper Errands", subtitle: "Applications and assigned errands", content: `${card(`<h2 class="cardTitle">Pick up printed thesis draft</h2>${badge("Assigned")}<div class="textMuted">Printing Pickup\nPrinting Services -> Room A305\nApplication: selected</div>${button("Accept")}${button("Messages", "secondary")}${button("Cancel/Withdraw", "secondary")}${button("Status Tracker", "secondary")}`)}${button("Back", "secondary")}` }),
  },
  {
    file: "11_applicants.png",
    html: shell({ title: "Applicants", subtitle: "Choose one helper", content: `${applicantCard("Juan Dela Cruz", "pending", "I can pick it up after my 12:30 class.", "25 minutes")}${applicantCard("Carlo Reyes", "pending", "I am already going to the printing area.", "20 minutes")}${button("Back", "secondary")}` }),
  },
  {
    file: "12_helper_profile_preview.png",
    html: shell({ title: "Helper Profile Preview", subtitle: "Juan Dela Cruz", content: `${card(`${badge("Public helper card")}<h2 class="cardTitle">Juan Dela Cruz</h2><div class="textMuted">Rating: 4.90\nCompleted errands: 12</div><div class="textMuted">Offer: I can pick it up after my 12:30 class.\nEstimate: 25 minutes</div>`)}${info("Privacy rule: student numbers and school email addresses are not displayed on public helper cards.")}${button("Back", "secondary")}` }),
  },
  {
    file: "13_errand_status.png",
    html: shell({ title: "Errand Status", subtitle: errand.title, content: `${statusTracker("Accepted")}${card(`<h2 class="cardTitle">Current status</h2>${badge("Accepted")}<div class="textMuted">Use the available action buttons below to move the errand through the prototype status flow.</div>`)}${button("Messages", "secondary")}${button("Cancel Errand", "secondary")}${button("Back", "secondary")}` }),
  },
  {
    file: "14_message_hub.png",
    html: shell({ title: "Messages", subtitle: "Chats open after a helper is selected", nav: "Messages", content: `${tabs(["All", "Errands", "System"], "All")}${info("Messaging is connected to a specific errand and is only visible to the requester and selected helper.")}${card(`<div class="row"><div style="width:42px;height:42px;border-radius:21px;background:#eaf3ff;display:grid;place-items:center;color:var(--primary);font-weight:800">C</div><div class="grow"><strong>My Posted Errand Chats</strong><div class="textMuted" style="font-size:12px">Open chats for errands where you selected a helper.</div></div>${badge("Requester")}</div>${button("Open", "secondary")}`)}${card(`<div class="row"><div style="width:42px;height:42px;border-radius:21px;background:#eaf3ff;display:grid;place-items:center;color:var(--primary);font-weight:800">C</div><div class="grow"><strong>My Helper Errand Chats</strong><div class="textMuted" style="font-size:12px">Open chats for errands assigned to you as helper.</div></div>${badge("Helper")}</div>${button("Open", "secondary")}`)}${button("Back", "secondary")}` }),
  },
  {
    file: "15_chat.png",
    html: shell({ title: "Messages", subtitle: errand.title, nav: "Messages", content: `${card(`<div class="chat"><strong>Maria Santos</strong>Hi Juan, the receipt is inside the envelope.</div><div class="chat mine"><strong>You</strong>Got it. I am at printing services now.</div><div class="chat"><strong>Maria Santos</strong>Thank you. Please drop it off at A305.</div>`)}<div class="row">${field("Message", "On my way to A305.", true)}<div class="button" style="width:92px">Send</div></div>${button("Back", "secondary")}` }),
  },
  {
    file: "16_completion_confirmation.png",
    html: shell({ title: "Completion Confirmation", subtitle: errand.title, content: `${info("Confirm only after the helper has completed the agreed errand. This moves the errand to requester-confirmed status.")}${statusTracker("Completed by Helper")}${button("Confirm Completion")}${button("Back", "secondary")}` }),
  },
  {
    file: "17_rate_helper.png",
    html: shell({ title: "Rate Helper", subtitle: errand.title, content: `${field("Rating 1-5", "5")}${field("Feedback", "Fast and clear updates. The printed draft was delivered safely.", true)}${button("Submit Rating")}${button("Back", "secondary")}` }),
  },
  {
    file: "18_report_errand.png",
    html: shell({ title: "Report Errand", subtitle: "Safety and moderation", content: `${info("Use reports for unsafe behavior, banned errands, restricted areas, harassment, privacy concerns, or school-rule violations.")}${field("Reason for reporting", "Unsafe or prohibited request")}${field("Additional details, optional", "The errand asks for a restricted document.", true)}${button("Submit Report", "danger")}${button("Back", "secondary")}` }),
  },
  {
    file: "19_report_user.png",
    html: shell({ title: "Report User", subtitle: "Safety and moderation", content: `${info("Use reports for unsafe behavior, banned errands, restricted areas, harassment, privacy concerns, or school-rule violations.")}${field("Reported user ID", "12")}${field("Reason for reporting", "Harassment or threat")}${field("Details", "User sent unsafe messages after the errand was cancelled.", true)}${button("Submit User Report", "danger")}${button("Back", "secondary")}` }),
  },
  {
    file: "20_profile.png",
    html: shell({ title: "Profile", subtitle: "Account summary and privacy-safe public data", nav: "Profile", content: `<div class="profileHero"><div class="avatar">U</div><h2 style="margin:0;font-size:20px">Juan Dela Cruz</h2>${badge("Verified Prototype", "white")}<div class="stats"><div class="stat">8<span>Completed</span></div><div class="stat">4.90<span>Rating</span></div><div class="stat">active<span>Account</span></div></div></div>${card(`<div class="row"><div class="grow"><strong>Edit Profile</strong><div class="textMuted" style="font-size:12px">Update your display name</div></div><strong>></strong></div>`)}${card(`<div class="row"><div class="grow"><strong>Privacy & Safety</strong><div class="textMuted" style="font-size:12px">Your email and student number stay hidden publicly</div></div><strong>></strong></div>`)}${card(`<div class="row"><div class="grow"><strong>History</strong><div class="textMuted" style="font-size:12px">Review completed and cancelled errands</div></div><strong>></strong></div>`)}${button("Back", "secondary")}` }),
  },
  {
    file: "21_edit_profile.png",
    html: shell({ title: "Edit Profile", subtitle: "Update account display details", nav: "Profile", content: `${info("Only safe profile fields are editable in this prototype. School email and student number stay private verification records.")}${field("Full name", "Juan Dela Cruz")}${button("Save Profile")}${button("Back", "secondary")}` }),
  },
  {
    file: "22_history.png",
    html: shell({ title: "History", subtitle: "Completed errands, ratings, and feedback", nav: "History", content: `${tabs(["All", "Posted", "As Helper"], "All")}${adminRecord("History Record", ["errand id: 18", "title: Buy bluebook before 2 PM", "status: Rated", "role: helper", "completed at: 2026-07-10 14:25:00"])}${button("Ratings")}${button("Back", "secondary")}` }),
  },
  {
    file: "23_user_ratings.png",
    html: shell({ title: "Ratings and Feedback", subtitle: "Your received feedback", nav: "History", content: `${adminRecord("Feedback", ["rating score: 5", "feedback: Fast and clear updates.", "rated by: Maria Santos", "errand: Pick up printed thesis draft"])}${adminRecord("Feedback", ["rating score: 4", "feedback: Delivered on time.", "rated by: Carlo Reyes", "errand: Classroom delivery"])}${button("Back", "secondary")}` }),
  },
  {
    file: "24_cancel_errand.png",
    html: shell({ title: "Cancel Errand", subtitle: errand.title, content: `${info("Cancellation is allowed before in-progress work. If the errand is already In Progress, a reason is required and saved in the status logs.")}${card(errandCore())}${field("Cancellation reason", "Class schedule changed; no longer needed.", true)}${button("Cancel Errand")}${button("Back", "secondary")}` }),
  },
  {
    file: "25_admin_dashboard.png",
    html: shell({ title: "Admin Dashboard", subtitle: "Monitoring and moderation", content: `${summaryGrid([["Total Users", "24"], ["Verified", "21"], ["Open", "6"], ["Active", "8"], ["Completed", "32"], ["Cancelled", "3"], ["Reported", "2"], ["Flagged", "4"]])}<div class="section">Admin Tools</div>${actionGrid([["Manage Users", "Search, restrict, deactivate"], ["Manage Errands", "View and remove records"], ["Reports", "Review and resolve"], ["Flagged Errands", "Moderation queue"], ["Ratings", "Feedback review"], ["Moderation", "Rule-based logs"]])}${button("Student Dashboard", "secondary")}${button("Logout", "secondary")}` }),
  },
  {
    file: "26_admin_manage_users.png",
    html: shell({ title: "Manage Users", subtitle: "Admin records", content: `${field("Search displayed user records", "maria")}${info("The current backend returns all users. This search field is a UI placeholder until server-side filtering is added.")}${adminRecord("Manage Users", ["user id: 3", "full name: Maria Santos", "role: student", "verification status: verified", "account status: active"])}${adminRecord("Manage Users", ["user id: 9", "full name: Suspicious Account", "role: student", "verification status: restricted", "account status: active"])}${button("Back", "secondary")}` }),
  },
  {
    file: "27_admin_manage_errands.png",
    html: shell({ title: "Manage Errands", subtitle: "Admin records", content: `${adminRecord("Manage Errands", ["errand id: 14", "title: Pick up printed thesis draft", "category: Printing Pickup", "status: Has Applicants", "requester name: Maria Santos"])}${adminRecord("Manage Errands", ["errand id: 20", "title: Buy bluebook before 2 PM", "category: Bluebook Purchase", "status: Open", "requester name: Carlo Reyes"])}${button("Back", "secondary")}` }),
  },
  {
    file: "28_admin_reports.png",
    html: shell({ title: "Reports", subtitle: "Admin records", content: `${adminRecord("Reports", ["report id: 5", "errand id: 17", "reporter id: 8", "reason: Restricted area", "status: pending"])}${adminRecord("Reports", ["report id: 6", "reported user id: 12", "reason: Harassment or threat", "status: pending"])}${button("Back", "secondary")}` }),
  },
  {
    file: "29_admin_flagged_errands.png",
    html: shell({ title: "Flagged Errands", subtitle: "Admin records", content: `${adminRecord("Flagged Errands", ["errand id: 17", "title: Pick up confidential document", "category: Document Delivery", "status: Flagged", "moderation result: rejected keyword"])}${adminRecord("Flagged Errands", ["errand id: 19", "title: Errand outside campus", "category: Nearby Establishment Errand", "status: Flagged", "moderation result: outside reasonable scope"])}${button("Back", "secondary")}` }),
  },
  {
    file: "30_admin_record_details.png",
    html: shell({ title: "Reports Details", subtitle: "Admin detail view", content: `${adminRecord("Reports", ["report id: 5", "errand id: 17", "reporter id: 8", "reason: Restricted area", "details: Request includes access to a restricted office.", "status: pending"], false)}${button("Remove Errand", "secondary")}${button("Resolve Report")}${button("Back", "secondary")}` }),
  },
  {
    file: "31_admin_ratings.png",
    html: shell({ title: "Ratings and Feedback", subtitle: "Admin review screen", content: `${info("This prototype keeps user ratings available through profile and history records. Add a backend endpoint for all ratings if the admin needs a full feedback export.")}${button("View Errand Records")}${button("Back", "secondary")}` }),
  },
  {
    file: "32_admin_moderation_logs.png",
    html: shell({ title: "Moderation Logs", subtitle: "Admin records", content: `${info("The backend does not currently have a moderation log endpoint, so this screen reuses flagged errands for the prototype.")}${adminRecord("Moderation Logs", ["errand id: 17", "title: Pick up confidential document", "matched keyword: confidential document", "decision: flagged"])}${adminRecord("Moderation Logs", ["errand id: 19", "title: Errand outside campus", "matched rule: outside campus scope", "decision: flagged"])}${button("Back", "secondary")}` }),
  },
];

(async () => {
  const browser = await chromium.launch();
  const page = await browser.newPage({ viewport: { width: 438, height: 920 }, deviceScaleFactor: 1 });
  for (const screen of screens) {
    await page.setContent(screen.html, { waitUntil: "load" });
    const phone = page.locator(".phone");
    await phone.screenshot({ path: path.join(outDir, screen.file) });
    console.log(`created ${screen.file}`);
  }
  await browser.close();
})();
