<?php
require_once "../config/db.php";
require_once "helpers.php";

$admin_id = $_GET["admin_id"] ?? null;
if (!$admin_id) {
    respond_error("Missing admin_id parameter.");
}
require_admin($pdo, (int) $admin_id);

$filter = strtolower(str_replace(" ", "_", trim($_GET["filter"] ?? "pending")));
$allowed_filters = ["pending", "resolved"];

$sql = "
    SELECT
        r.*,
        reporter.full_name AS reported_by_name,
        reported.full_name AS reported_user_name,
        e.title AS errand_title
    FROM reports r
    JOIN users reporter ON r.reported_by_user_id = reporter.user_id
    LEFT JOIN users reported ON r.reported_user_id = reported.user_id
    LEFT JOIN errands e ON r.errand_id = e.errand_id
    WHERE 1 = 1
";
$params = [];

if (in_array($filter, $allowed_filters, true)) {
    $sql .= " AND r.status = ?";
    $params[] = $filter;
}

$sql .= " ORDER BY r.created_at DESC";
$stmt = $pdo->prepare($sql);
$stmt->execute($params);

respond_success("Reports retrieved.", $stmt->fetchAll(PDO::FETCH_ASSOC));
?>
