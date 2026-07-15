<?php
require_once "../config/db.php";
require_once "helpers.php";

$user_id = $_GET["user_id"] ?? null;
$keyword = trim($_GET["keyword"] ?? "");
$category = trim($_GET["category"] ?? "");

$sql = "
    SELECT e.*, u.full_name AS requester_name, u.average_rating AS requester_rating
    FROM errands e
    JOIN users u ON e.requester_id = u.user_id
    WHERE e.status IN ('Open', 'Has Applicants')
    AND e.moderation_status = 'allowed'
";

$params = [];

if ($user_id) {
    $sql .= " AND e.requester_id <> ?";
    $params[] = $user_id;
    
    $sql .= " AND NOT EXISTS (
                SELECT 1 FROM errand_applications ea 
                WHERE ea.errand_id = e.errand_id 
                AND ea.helper_id = ?
              )";
    $params[] = $user_id;
}

if ($keyword !== "") {
    $sql .= " AND (e.title LIKE ? OR e.description LIKE ? OR e.pickup_location LIKE ? OR e.dropoff_location LIKE ?)";
    $like = "%" . $keyword . "%";
    array_push($params, $like, $like, $like, $like);
}

if ($category !== "") {
    $sql .= " AND e.category = ?";
    $params[] = $category;
}

$sql .= " ORDER BY e.created_at DESC";

$stmt = $pdo->prepare($sql);
$stmt->execute($params);
$errands = $stmt->fetchAll(PDO::FETCH_ASSOC);

respond_success("Available errands retrieved.", $errands);
?>