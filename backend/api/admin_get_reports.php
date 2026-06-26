<?php
require_once "../config/db.php";
require_once "helpers.php";

$admin_id = $_GET["admin_id"] ?? null;
if ($admin_id) {
    require_admin($pdo, $admin_id);
}

$stmt = $pdo->query("
    SELECT
        r.*,
        reporter.full_name AS reported_by_name,
        reported.full_name AS reported_user_name,
        e.title AS errand_title
    FROM reports r
    JOIN users reporter ON r.reported_by_user_id = reporter.user_id
    LEFT JOIN users reported ON r.reported_user_id = reported.user_id
    LEFT JOIN errands e ON r.errand_id = e.errand_id
    ORDER BY r.created_at DESC
");

respond_success("Reports retrieved.", $stmt->fetchAll(PDO::FETCH_ASSOC));
?>
