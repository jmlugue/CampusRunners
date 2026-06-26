<?php
require_once "../config/db.php";
require_once "helpers.php";

$errand_id = $_GET["errand_id"] ?? null;
$requester_id = $_GET["requester_id"] ?? null;

if (!$errand_id) {
    respond_error("Errand ID is required.");
}

$errand = get_errand_by_id($pdo, $errand_id);
if (!$errand) {
    respond_error("Errand not found.");
}

if ($requester_id && (int) $errand["requester_id"] !== (int) $requester_id) {
    respond_error("Only the requester can view applicants.");
}

$stmt = $pdo->prepare("
    SELECT
        a.application_id,
        a.errand_id,
        a.helper_id,
        a.offer_note,
        a.estimated_completion_time,
        a.status,
        a.created_at,
        u.full_name AS helper_name,
        u.average_rating,
        u.completed_errands
    FROM errand_applications a
    JOIN users u ON a.helper_id = u.user_id
    WHERE a.errand_id = ?
    ORDER BY a.created_at ASC
");
$stmt->execute([$errand_id]);

respond_success("Applicants retrieved.", $stmt->fetchAll(PDO::FETCH_ASSOC));
?>
