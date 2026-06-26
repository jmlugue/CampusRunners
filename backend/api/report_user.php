<?php
require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();
require_fields($data, ["reported_user_id", "reported_by_user_id", "reason"]);

$reported_user_id = (int) $data["reported_user_id"];
$reported_by_user_id = (int) $data["reported_by_user_id"];
$reason = trim($data["reason"]);
$details = trim($data["details"] ?? "");
$errand_id = isset($data["errand_id"]) && $data["errand_id"] !== "" ? (int) $data["errand_id"] : null;

$stmt = $pdo->prepare("
    INSERT INTO reports (errand_id, reported_user_id, reported_by_user_id, report_type, reason, details)
    VALUES (?, ?, ?, 'user', ?, ?)
");
$stmt->execute([$errand_id, $reported_user_id, $reported_by_user_id, $reason, $details]);

respond_success("User report submitted.", [
    "report_id" => $pdo->lastInsertId()
]);
?>
