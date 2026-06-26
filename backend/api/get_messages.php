<?php
require_once "../config/db.php";
require_once "helpers.php";

$errand_id = $_GET["errand_id"] ?? null;
$user_id = $_GET["user_id"] ?? null;

if (!$errand_id || !$user_id) {
    respond_error("Errand ID and user ID are required.");
}

$errand = get_errand_by_id($pdo, $errand_id);
if (!$errand || !user_can_access_errand($errand, $user_id)) {
    respond_error("You cannot view messages for this errand.");
}

$stmt = $pdo->prepare("
    SELECT m.*, sender.full_name AS sender_name, receiver.full_name AS receiver_name
    FROM messages m
    JOIN users sender ON m.sender_id = sender.user_id
    JOIN users receiver ON m.receiver_id = receiver.user_id
    WHERE m.errand_id = ?
    ORDER BY m.created_at ASC
");
$stmt->execute([$errand_id]);

respond_success("Messages retrieved.", $stmt->fetchAll(PDO::FETCH_ASSOC));
?>
