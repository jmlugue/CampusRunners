<?php
require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();
require_fields($data, ["admin_id", "report_id", "status"]);

$admin_id = require_positive_int($data["admin_id"], "Admin ID");
$report_id = require_positive_int($data["report_id"], "Report ID");
$status = trim($data["status"]);
$details = validate_optional_text_length($data["details"] ?? "", "Resolution details", 500);

require_admin($pdo, $admin_id);

if ($status !== "resolved") {
    respond_error("Invalid report status.");
}

$check = $pdo->prepare("SELECT report_id FROM reports WHERE report_id = ? LIMIT 1");
$check->execute([$report_id]);
if (!$check->fetch(PDO::FETCH_ASSOC)) {
    respond_error("Report was not found.");
}

$stmt = $pdo->prepare("
    UPDATE reports
    SET status = ?, resolved_at = NOW()
    WHERE report_id = ?
");
$stmt->execute([$status, $report_id]);

$action = $pdo->prepare("
    INSERT INTO admin_actions (admin_id, action_type, action_details)
    VALUES (?, 'resolve_report', ?)
");
$action->execute([$admin_id, "Report #" . $report_id . " set to " . $status . ". " . $details]);

respond_success("Report status updated.");
?>
