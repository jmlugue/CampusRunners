<?php
// backend/api/get_moderation_logs.php
// Returns moderation logs for admin review.
require_once "../config/db.php";
require_once "helpers.php";

$admin_id = $_GET["admin_id"] ?? null;

if (!$admin_id) {
    respond_error("Missing admin_id parameter.");
}

require_admin($pdo, (int) $admin_id);

$result_filter = trim($_GET["result"] ?? "");

if ($result_filter !== "" && in_array($result_filter, ["allowed", "flagged", "rejected"])) {
    $stmt = $pdo->prepare("
        SELECT ml.*, e.title AS errand_title, e.status AS errand_status
        FROM moderation_logs ml
        LEFT JOIN errands e ON ml.errand_id = e.errand_id
        WHERE ml.result = ?
        ORDER BY ml.created_at DESC
    ");
    $stmt->execute([$result_filter]);
} else {
    $stmt = $pdo->prepare("
        SELECT ml.*, e.title AS errand_title, e.status AS errand_status
        FROM moderation_logs ml
        LEFT JOIN errands e ON ml.errand_id = e.errand_id
        ORDER BY ml.created_at DESC
    ");
    $stmt->execute();
}

$logs = $stmt->fetchAll(PDO::FETCH_ASSOC);

respond_success("Moderation logs loaded.", $logs);
?>
