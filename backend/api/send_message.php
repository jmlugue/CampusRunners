<?php
require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();
require_fields($data, ["errand_id", "sender_id", "receiver_id", "message_text"]);

$errand_id = (int) $data["errand_id"];
$sender_id = (int) $data["sender_id"];
$receiver_id = (int) $data["receiver_id"];
$message_text = trim($data["message_text"]);

$errand = get_errand_by_id($pdo, $errand_id);
if (!$errand) {
    respond_error("Errand not found.");
}

if (!$errand["selected_helper_id"]) {
    respond_error("Messaging is available only after a helper is selected.");
}

if (!user_can_access_errand($errand, $sender_id) || !user_can_access_errand($errand, $receiver_id)) {
    respond_error("Only the requester and selected helper can message each other.");
}

$stmt = $pdo->prepare("
    INSERT INTO messages (errand_id, sender_id, receiver_id, message_text)
    VALUES (?, ?, ?, ?)
");
$stmt->execute([$errand_id, $sender_id, $receiver_id, $message_text]);

respond_success("Message sent.", [
    "message_id" => $pdo->lastInsertId()
]);
?>
