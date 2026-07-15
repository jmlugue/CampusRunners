<?php
require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();
require_fields($data, ["errand_id", "helper_id"]);

$errand_id = require_positive_int($data["errand_id"], "Errand ID");
$helper_id = require_positive_int($data["helper_id"], "Helper ID");
$offer_note = require_text_length($data["offer_note"] ?? "", "Offer note", 10, 500);
$estimated_completion_time = require_text_length($data["estimated_completion_time"] ?? "", "Estimated completion time", 2, 100);

validate_active_verified_student($pdo, $helper_id, "Helper");

$errand = get_errand_by_id($pdo, $errand_id);
if (!$errand) {
    respond_error("Errand not found.");
}

if ((int) $errand["requester_id"] === $helper_id) {
    respond_error("You cannot apply to your own errand.");
}

if (!in_array($errand["status"], ["Open", "Has Applicants"])) {
    respond_error("This errand is no longer open for applications.");
}

if ($errand["moderation_status"] !== "allowed") {
    respond_error("This errand is not available for applications.");
}

try {
    $pdo->beginTransaction();

    $stmt = $pdo->prepare("
        INSERT INTO errand_applications (errand_id, helper_id, offer_note, estimated_completion_time, status)
        VALUES (?, ?, ?, ?, 'pending')
    ");
    $stmt->execute([$errand_id, $helper_id, $offer_note, $estimated_completion_time]);
    $application_id = $pdo->lastInsertId();

    if ($errand["status"] === "Open") {
        $update = $pdo->prepare("UPDATE errands SET status = 'Has Applicants' WHERE errand_id = ?");
        $update->execute([$errand_id]);
        log_status_change($pdo, $errand_id, $helper_id, "Open", "Has Applicants", "Helper application submitted.");
    }

    $pdo->commit();
    respond_success("Application submitted.", [
        "application_id" => $application_id
    ]);
} catch (PDOException $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    respond_error("Application failed. You may have already applied.");
}
?>
