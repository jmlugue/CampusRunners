<?php
require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();
require_fields($data, ["errand_id", "reported_by_user_id", "reason"]);

$errand_id = (int) $data["errand_id"];
$reported_by_user_id = (int) $data["reported_by_user_id"];
$reason = trim($data["reason"]);
$details = trim($data["details"] ?? "");

$stmt = $pdo->prepare("
    INSERT INTO reports (errand_id, reported_by_user_id, report_type, reason, details)
    VALUES (?, ?, 'errand', ?, ?)
");
$stmt->execute([$errand_id, $reported_by_user_id, $reason, $details]);
$report_id = $pdo->lastInsertId();

$errand = get_errand_by_id($pdo, $errand_id);
if ($errand) {
    update_errand_status($pdo, $errand_id, $reported_by_user_id, "Reported", $reason);
}

respond_success("Errand report submitted.", [
    "report_id" => $report_id
]);
?>
