<?php
require_once "../config/db.php";
require_once "helpers.php";

$user_id = $_GET["user_id"] ?? null;

if (!$user_id) {
    respond_error("User ID is required.");
}

$user = sanitize_user(get_user_by_id($pdo, $user_id));
if (!$user) {
    respond_error("User not found.");
}

respond_success("User profile retrieved.", $user);
?>
