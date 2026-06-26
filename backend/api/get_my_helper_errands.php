<?php
require_once "../config/db.php";
require_once "helpers.php";

$user_id = $_GET["user_id"] ?? null;

if (!$user_id) {
    respond_error("User ID is required.");
}

$stmt = $pdo->prepare("
    SELECT
        e.*,
        requester.full_name AS requester_name,
        a.application_id,
        a.status AS application_status,
        a.offer_note,
        a.estimated_completion_time
    FROM errand_applications a
    JOIN errands e ON a.errand_id = e.errand_id
    JOIN users requester ON e.requester_id = requester.user_id
    WHERE a.helper_id = ?
    ORDER BY a.created_at DESC
");
$stmt->execute([$user_id]);

respond_success("Helper errands retrieved.", $stmt->fetchAll(PDO::FETCH_ASSOC));
?>
