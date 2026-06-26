<?php
require_once "../config/db.php";
require_once "helpers.php";

$user_id = $_GET["user_id"] ?? null;

if (!$user_id) {
    respond_error("User ID is required.");
}

$stmt = $pdo->prepare("
    SELECT e.*
    FROM errands e
    WHERE e.requester_id = ? OR e.selected_helper_id = ?
    ORDER BY e.created_at DESC
");
$stmt->execute([$user_id, $user_id]);

respond_success("User history retrieved.", $stmt->fetchAll(PDO::FETCH_ASSOC));
?>
