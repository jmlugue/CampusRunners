<?php
require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();
require_fields($data, ["reported_user_id", "reported_by_user_id", "reason"]);

$reported_user_id = require_positive_int($data["reported_user_id"], "Reported user ID");
$reported_by_user_id = require_positive_int($data["reported_by_user_id"], "Reporter ID");
$reason = require_text_length($data["reason"], "Report reason", 5, 120);
$details = require_text_length($data["details"] ?? "", "Report details", 10, 1000);
$errand_id = isset($data["errand_id"]) && $data["errand_id"] !== ""
    ? require_positive_int($data["errand_id"], "Errand ID")
    : null;

if ($reported_user_id === $reported_by_user_id) {
    respond_error("You cannot report your own account.");
}

$reporter = get_user_by_id($pdo, $reported_by_user_id);
if (!$reporter || $reporter["account_status"] !== "active") {
    respond_error("This account cannot submit reports.");
}

if (!get_user_by_id($pdo, $reported_user_id)) {
    respond_error("Reported user account was not found.");
}

if ($errand_id !== null && !get_errand_by_id($pdo, $errand_id)) {
    respond_error("Errand not found.");
}

$stmt = $pdo->prepare("
    INSERT INTO reports (errand_id, reported_user_id, reported_by_user_id, report_type, reason, details)
    VALUES (?, ?, ?, 'user', ?, ?)
");
$stmt->execute([$errand_id, $reported_user_id, $reported_by_user_id, $reason, $details]);

respond_success("User report submitted.", [
    "report_id" => $pdo->lastInsertId()
]);
?>
