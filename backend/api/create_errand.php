<?php
require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();

$requester_id = $data["requester_id"] ?? null;
$reward_amount = $data["reward_amount"] ?? null;

if (
    !$requester_id ||
    trim($data["title"] ?? "") === "" ||
    trim($data["description"] ?? "") === "" ||
    trim($data["category"] ?? "") === "" ||
    trim($data["pickup_location"] ?? "") === "" ||
    trim($data["dropoff_location"] ?? "") === "" ||
    trim($data["deadline"] ?? "") === ""
) {
    respond_error("Missing required errand fields.");
}

$requester_id = require_positive_int($requester_id, "Requester ID");
validate_active_verified_student($pdo, $requester_id, "Requester");

$title = require_text_length($data["title"], "Title", 5, 120);
$description = require_text_length($data["description"], "Description", 15, 1000);
$category = validate_errand_category($data["category"]);
$pickup_location = require_text_length($data["pickup_location"], "Pickup location", 3, 150);
$dropoff_location = require_text_length($data["dropoff_location"], "Drop-off location", 3, 150);
$deadline = validate_deadline($data["deadline"]);
$reward_amount = validate_reward_amount($reward_amount);
$reward_note = validate_optional_text_length($data["reward_note"] ?? "", "Reward note", 180);

validate_room_location("Pickup", $pickup_location);
validate_room_location("Drop-off", $dropoff_location);

$moderation = moderate_errand($title, $description, $category, $pickup_location, $dropoff_location);
$moderation_status = $moderation["result"];

try {
    $stmt = $pdo->prepare("
        INSERT INTO errands
        (requester_id, title, description, category, pickup_location, dropoff_location, deadline, reward_amount, reward_note, status, moderation_status)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'Open', ?)
    ");
    $stmt->execute([
        $requester_id,
        $title,
        $description,
        $category,
        $pickup_location,
        $dropoff_location,
        $deadline,
        $reward_amount,
        $reward_note,
        $moderation_status
    ]);

    $errand_id = $pdo->lastInsertId();

    $log = $pdo->prepare("
        INSERT INTO moderation_logs (errand_id, checked_title, checked_description, result, matched_terms)
        VALUES (?, ?, ?, ?, ?)
    ");
    $log->execute([$errand_id, $title, $description, $moderation_status, $moderation["matched_terms"]]);

    if ($moderation_status === "flagged") {
        respond_success("Errand posted but flagged for admin review.", [
            "errand_id" => $errand_id,
            "moderation_status" => $moderation_status
        ]);
    }

    respond_success("Errand created successfully.", [
        "errand_id" => $errand_id,
        "moderation_status" => $moderation_status
    ]);
} catch (PDOException $e) {
    respond_error("Failed to create errand.");
}
?>
