<?php
require_once "../config/db.php";
require_once "helpers.php";

$errand_id = $_GET["errand_id"] ?? null;

if (!$errand_id) {
    respond_error("Errand ID is required.");
}

$stmt = $pdo->prepare("
    SELECT
        e.*,
        requester.full_name AS requester_name,
        requester.average_rating AS requester_rating,
        helper.full_name AS helper_name,
        helper.average_rating AS helper_rating
    FROM errands e
    JOIN users requester ON e.requester_id = requester.user_id
    LEFT JOIN users helper ON e.selected_helper_id = helper.user_id
    WHERE e.errand_id = ?
    LIMIT 1
");
$stmt->execute([$errand_id]);
$errand = $stmt->fetch(PDO::FETCH_ASSOC);

if (!$errand) {
    respond_error("Errand not found.");
}

$logs = $pdo->prepare("
    SELECT l.*, u.full_name AS changed_by_name
    FROM errand_status_logs l
    JOIN users u ON l.changed_by = u.user_id
    WHERE l.errand_id = ?
    ORDER BY l.created_at ASC
");
$logs->execute([$errand_id]);
$errand["status_logs"] = $logs->fetchAll(PDO::FETCH_ASSOC);

respond_success("Errand details retrieved.", $errand);
?>
