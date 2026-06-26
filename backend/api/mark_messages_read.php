<?php
require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();
require_fields($data, ["errand_id", "user_id"]);

$errand_id = (int) $data["errand_id"];
$user_id = (int) $data["user_id"];

$errand = get_errand_by_id($pdo, $errand_id);
if (!$errand || !user_can_access_errand($errand, $user_id)) {
    respond_error("You cannot update messages for this errand.");
}

$stmt = $pdo->prepare("UPDATE messages SET is_read = 1 WHERE errand_id = ? AND receiver_id = ?");
$stmt->execute([$errand_id, $user_id]);

respond_success("Messages marked as read.");
?>
