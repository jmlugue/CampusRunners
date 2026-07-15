<?php
require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();
require_fields($data, ["errand_id", "rated_user_id", "rated_by_user_id", "rating_score"]);

$errand_id = require_positive_int($data["errand_id"], "Errand ID");
$rated_user_id = require_positive_int($data["rated_user_id"], "Rated user ID");
$rated_by_user_id = require_positive_int($data["rated_by_user_id"], "Reviewer ID");
$rating_score = (int) $data["rating_score"];
$feedback = validate_optional_text_length($data["feedback"] ?? "", "Feedback", 500);

if ($rating_score < 1 || $rating_score > 5) {
    respond_error("Rating score must be from 1 to 5.");
}

$errand = get_errand_by_id($pdo, $errand_id);
if (!$errand) {
    respond_error("Errand not found.");
}

if ((int) $errand["requester_id"] !== $rated_by_user_id || (int) $errand["selected_helper_id"] !== $rated_user_id) {
    respond_error("Only the requester can rate the selected helper.");
}

if ($errand["status"] !== "Confirmed by Requester") {
    respond_error("Completion must be confirmed before rating.");
}

try {
    $pdo->beginTransaction();

    $stmt = $pdo->prepare("
        INSERT INTO ratings (errand_id, rated_user_id, rated_by_user_id, rating_score, feedback)
        VALUES (?, ?, ?, ?, ?)
    ");
    $stmt->execute([$errand_id, $rated_user_id, $rated_by_user_id, $rating_score, $feedback]);

    $avg = $pdo->prepare("
        UPDATE users
        SET average_rating = (
            SELECT ROUND(AVG(rating_score), 2) FROM ratings WHERE rated_user_id = ?
        ),
        completed_errands = completed_errands + 1
        WHERE user_id = ?
    ");
    $avg->execute([$rated_user_id, $rated_user_id]);

    $pdo->prepare("UPDATE errands SET status = 'Closed' WHERE errand_id = ?")->execute([$errand_id]);
    log_status_change($pdo, $errand_id, $rated_by_user_id, "Confirmed by Requester", "Closed", "Requester submitted rating.");

    $pdo->commit();
    respond_success("Rating submitted.");
} catch (PDOException $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    respond_error("Rating failed. This errand may already have a rating.");
}
?>
