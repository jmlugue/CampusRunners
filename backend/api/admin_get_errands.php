<?php
require_once "../config/db.php";
require_once "helpers.php";

$admin_id = $_GET["admin_id"] ?? null;
if ($admin_id) {
    require_admin($pdo, $admin_id);
}

$status = trim($_GET["status"] ?? "");
$keyword = trim($_GET["keyword"] ?? "");

$sql = "
    SELECT e.*, requester.full_name AS requester_name, helper.full_name AS helper_name
    FROM errands e
    JOIN users requester ON e.requester_id = requester.user_id
    LEFT JOIN users helper ON e.selected_helper_id = helper.user_id
    WHERE 1 = 1
";
$params = [];

if ($status !== "") {
    $sql .= " AND e.status = ?";
    $params[] = $status;
}

if ($keyword !== "") {
    $sql .= " AND (e.title LIKE ? OR e.description LIKE ? OR requester.full_name LIKE ?)";
    $like = "%" . $keyword . "%";
    array_push($params, $like, $like, $like);
}

$sql .= " ORDER BY e.created_at DESC";
$stmt = $pdo->prepare($sql);
$stmt->execute($params);

respond_success("Errands retrieved.", $stmt->fetchAll(PDO::FETCH_ASSOC));
?>
