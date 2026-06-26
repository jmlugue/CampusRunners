<?php
require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();
require_fields($data, ["user_id", "full_name"]);

$user_id = (int) $data["user_id"];
$full_name = trim($data["full_name"]);

$stmt = $pdo->prepare("UPDATE users SET full_name = ? WHERE user_id = ?");
$stmt->execute([$full_name, $user_id]);

respond_success("Profile updated.");
?>
