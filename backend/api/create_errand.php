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

$pickup_room_error = validate_room_location("Pickup", $pickup_location);
if ($pickup_room_error !== null) {
    respond_error($pickup_room_error);
}

$dropoff_room_error = validate_room_location("Drop-off", $dropoff_location);
if ($dropoff_room_error !== null) {
    respond_error($dropoff_room_error);
}

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

function validate_room_location($label, $location) {
    preg_match_all('/\b([REre])[- ]?(\d{3})\b/', $location, $matches, PREG_SET_ORDER);

    foreach ($matches as $match) {
        $room_number = (int) $match[2];
        if (!is_valid_mcl_room_number($room_number)) {
            return $label . " room must be from R101-R113, E101-E113, up to R501-R513 or E501-E513.";
        }
    }

    $without_prefixed_rooms = preg_replace('/\b([REre])[- ]?(\d{3})\b/', '', $location);
    if (preg_match('/\b([1-5][0-9]{2})\b/', $without_prefixed_rooms)) {
        return $label . " room code must start with R or E, example R101 or E413.";
    }

    return null;
}

function is_valid_mcl_room_number($room_number) {
    $floor = intdiv($room_number, 100);
    $room = $room_number % 100;
    return $floor >= 1 && $floor <= 5 && $room >= 1 && $room <= 13;
}
?>
