<?php
require_once "../config/db.php";
require_once "helpers.php";

$user_id = $_GET["user_id"] ?? null;

if (!$user_id) {
    respond_error("User ID is required.");
}

$stmt = $pdo->prepare("
    SELECT e.*, helper.full_name AS helper_name
    FROM errands e
    LEFT JOIN users helper ON e.selected_helper_id = helper.user_id
    WHERE e.requester_id = ?
    ORDER BY e.created_at DESC
");
$stmt->execute([$user_id]);

respond_success("Posted errands retrieved.", $stmt->fetchAll(PDO::FETCH_ASSOC));
?>
