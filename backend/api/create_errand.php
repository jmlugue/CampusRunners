<?php
require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();

$requester_id = $data["requester_id"] ?? null;
$title = trim($data["title"] ?? "");
$description = trim($data["description"] ?? "");
$category = trim($data["category"] ?? "");
$pickup_location = trim($data["pickup_location"] ?? "");
$dropoff_location = trim($data["dropoff_location"] ?? "");
$deadline = trim($data["deadline"] ?? "");
$reward_amount = $data["reward_amount"] ?? null;
$reward_note = trim($data["reward_note"] ?? "");

if (!$requester_id || $title === "" || $description === "" || $category === "" || $pickup_location === "" || $dropoff_location === "" || $deadline === "") {
    respond_error("Missing required errand fields.");
}

$moderation = moderate_errand($title, $description);
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
