<?php
require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();
require_fields($data, ["errand_id", "sender_id", "receiver_id", "message_text"]);

$errand_id = require_positive_int($data["errand_id"], "Errand ID");
$sender_id = require_positive_int($data["sender_id"], "Sender ID");
$receiver_id = require_positive_int($data["receiver_id"], "Receiver ID");
$message_text = require_text_length($data["message_text"], "Message", 1, 500);

if ($sender_id === $receiver_id) {
    respond_error("Sender and receiver must be different users.");
}

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
