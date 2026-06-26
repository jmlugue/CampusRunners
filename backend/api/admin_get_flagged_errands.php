<?php
require_once "../config/db.php";
require_once "helpers.php";

$admin_id = $_GET["admin_id"] ?? null;
if ($admin_id) {
    require_admin($pdo, $admin_id);
}

$stmt = $pdo->query("
    SELECT e.*, u.full_name AS requester_name, m.matched_terms, m.created_at AS moderated_at
    FROM errands e
    JOIN users u ON e.requester_id = u.user_id
    LEFT JOIN moderation_logs m ON e.errand_id = m.errand_id
    WHERE e.moderation_status = 'flagged'
    ORDER BY e.created_at DESC
");

respond_success("Flagged errands retrieved.", $stmt->fetchAll(PDO::FETCH_ASSOC));
?>
