<?php
require_once "../config/db.php";
require_once "helpers.php";

$user_id = $_GET["user_id"] ?? null;

if (!$user_id) {
    respond_error("User ID is required.");
}

$stmt = $pdo->prepare("
    SELECT r.*, e.title AS errand_title, u.full_name AS rated_by_name
    FROM ratings r
    JOIN errands e ON r.errand_id = e.errand_id
    JOIN users u ON r.rated_by_user_id = u.user_id
    WHERE r.rated_user_id = ?
    ORDER BY r.created_at DESC
");
$stmt->execute([$user_id]);

respond_success("Ratings retrieved.", $stmt->fetchAll(PDO::FETCH_ASSOC));
?>
