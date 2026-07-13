<?php
// backend/api/get_student_stats.php
// Returns dashboard statistics for a specific student.
require_once "../config/db.php";
require_once "helpers.php";

$user_id = $_GET["user_id"] ?? null;

if (!$user_id) {
    respond_error("Missing user_id parameter.");
}

$user_id = (int) $user_id;

$user = get_user_by_id($pdo, $user_id);
if (!$user) {
    respond_error("User not found.");
}

// Count errands posted by this user in various states
$stmt = $pdo->prepare("SELECT status, COUNT(*) as count FROM errands WHERE requester_id = ? GROUP BY status");
$stmt->execute([$user_id]);
$status_counts = $stmt->fetchAll(PDO::FETCH_ASSOC);

$open = 0;
$active = 0;
$completed = 0;
$cancelled = 0;

foreach ($status_counts as $row) {
    $s = $row["status"];
    $c = (int) $row["count"];
    if ($s === "Open" || $s === "Has Applicants") {
        $open += $c;
    } elseif (in_array($s, ["Assigned", "Accepted", "In Progress", "Completed by Helper"])) {
        $active += $c;
    } elseif (in_array($s, ["Confirmed by Requester", "Rated", "Closed"])) {
        $completed += $c;
    } elseif (strpos($s, "Cancel") !== false) {
        $cancelled += $c;
    }
}

// Count helper errands
$stmt = $pdo->prepare("
    SELECT COUNT(*) as count FROM errand_applications
    WHERE helper_id = ? AND status = 'selected'
");
$stmt->execute([$user_id]);
$helper_active = (int) $stmt->fetchColumn();

// Count completed helper errands
$stmt = $pdo->prepare("
    SELECT COUNT(*) as count FROM errands
    WHERE selected_helper_id = ? AND status IN ('Confirmed by Requester', 'Rated', 'Closed')
");
$stmt->execute([$user_id]);
$helper_completed = (int) $stmt->fetchColumn();

// Pending applications
$stmt = $pdo->prepare("
    SELECT COUNT(*) as count FROM errand_applications
    WHERE helper_id = ? AND status = 'pending'
");
$stmt->execute([$user_id]);
$pending_applications = (int) $stmt->fetchColumn();

// Unread messages
$stmt = $pdo->prepare("
    SELECT COUNT(*) as count FROM messages
    WHERE receiver_id = ? AND is_read = 0
");
$stmt->execute([$user_id]);
$unread_messages = (int) $stmt->fetchColumn();

respond_success("Student statistics loaded.", [
    "open_errands" => $open,
    "active_errands" => $active,
    "completed_errands" => $completed,
    "cancelled_errands" => $cancelled,
    "helper_active" => $helper_active,
    "helper_completed" => $helper_completed,
    "pending_applications" => $pending_applications,
    "unread_messages" => $unread_messages,
    "average_rating" => $user["average_rating"],
    "total_completed" => $user["completed_errands"]
]);
?>
