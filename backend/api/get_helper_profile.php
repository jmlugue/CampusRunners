<?php
require_once "../config/db.php";
require_once "helpers.php";

$helper_id = $_GET["helper_id"] ?? null;

if (!$helper_id) {
    respond_error("Helper ID is required.");
}

$stmt = $pdo->prepare("
    SELECT user_id, full_name, average_rating, completed_errands, verification_status, account_status
    FROM users
    WHERE user_id = ? AND role = 'student'
    LIMIT 1
");
$stmt->execute([$helper_id]);
$helper = $stmt->fetch(PDO::FETCH_ASSOC);

if (!$helper) {
    respond_error("Helper not found.");
}

respond_success("Helper profile retrieved.", $helper);
?>
