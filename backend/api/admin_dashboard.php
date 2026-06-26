<?php
require_once "../config/db.php";
require_once "helpers.php";

$admin_id = $_GET["admin_id"] ?? null;
if ($admin_id) {
    require_admin($pdo, $admin_id);
}

$counts = [];
$queries = [
    "total_users" => "SELECT COUNT(*) FROM users",
    "verified_users" => "SELECT COUNT(*) FROM users WHERE verification_status = 'verified'",
    "open_errands" => "SELECT COUNT(*) FROM errands WHERE status IN ('Open', 'Has Applicants')",
    "active_errands" => "SELECT COUNT(*) FROM errands WHERE status IN ('Assigned', 'Accepted', 'In Progress', 'Completed by Helper')",
    "completed_errands" => "SELECT COUNT(*) FROM errands WHERE status IN ('Confirmed by Requester', 'Closed')",
    "cancelled_errands" => "SELECT COUNT(*) FROM errands WHERE status IN ('Cancelled by Requester', 'Cancelled by Helper')",
    "reported_errands" => "SELECT COUNT(*) FROM errands WHERE status = 'Reported'",
    "flagged_errands" => "SELECT COUNT(*) FROM errands WHERE moderation_status = 'flagged'"
];

foreach ($queries as $key => $sql) {
    $counts[$key] = (int) $pdo->query($sql)->fetchColumn();
}

respond_success("Admin dashboard counts retrieved.", $counts);
?>
