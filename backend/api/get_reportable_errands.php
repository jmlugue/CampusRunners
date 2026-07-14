<?php
require_once "../config/db.php";
require_once "helpers.php";

$user_id = (int) ($_GET["user_id"] ?? 0);
$keyword = trim($_GET["keyword"] ?? "");

if (!$user_id || !get_user_by_id($pdo, $user_id)) {
    respond_error("A valid user_id parameter is required.");
}

$sql = "
    SELECT e.errand_id, e.requester_id, e.title, e.description, e.category,
           e.pickup_location, e.dropoff_location, e.deadline, e.status,
           e.created_at, requester.full_name AS requester_name
    FROM errands e
    JOIN users requester ON e.requester_id = requester.user_id
    WHERE e.requester_id <> ?
      AND e.status <> 'Removed by Admin'
";
$params = [$user_id];

if ($keyword !== "") {
    $sql .= " AND (e.title LIKE ? OR e.category LIKE ? OR e.pickup_location LIKE ? OR e.dropoff_location LIKE ?)";
    $like = "%" . $keyword . "%";
    array_push($params, $like, $like, $like, $like);
}

$sql .= " ORDER BY e.created_at DESC LIMIT 50";
$stmt = $pdo->prepare($sql);
$stmt->execute($params);

respond_success("Reportable errands retrieved.", $stmt->fetchAll(PDO::FETCH_ASSOC));
?>
