<?php
require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();
require_fields($data, ["user_id", "full_name"]);

$user_id = require_positive_int($data["user_id"], "User ID");
$full_name = validate_full_name($data["full_name"]);

if (!get_user_by_id($pdo, $user_id)) {
    respond_error("User account was not found.");
}

$stmt = $pdo->prepare("UPDATE users SET full_name = ? WHERE user_id = ?");
$stmt->execute([$full_name, $user_id]);

respond_success("Profile updated.");
?>
