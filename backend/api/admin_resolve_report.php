<?php
require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();
require_fields($data, ["admin_id", "report_id", "status"]);

$admin_id = (int) $data["admin_id"];
$report_id = (int) $data["report_id"];
$status = trim($data["status"]);
$details = trim($data["details"] ?? "");

require_admin($pdo, $admin_id);

if (!in_array($status, ["under_review", "resolved", "dismissed"])) {
    respond_error("Invalid report status.");
}

$stmt = $pdo->prepare("
    UPDATE reports
    SET status = ?, resolved_at = CASE WHEN ? IN ('resolved', 'dismissed') THEN NOW() ELSE resolved_at END
    WHERE report_id = ?
");
$stmt->execute([$status, $status, $report_id]);

$action = $pdo->prepare("
    INSERT INTO admin_actions (admin_id, action_type, action_details)
    VALUES (?, 'resolve_report', ?)
");
$action->execute([$admin_id, "Report #" . $report_id . " set to " . $status . ". " . $details]);

respond_success("Report status updated.");
?>
