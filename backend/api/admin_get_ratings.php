<?php
require_once "../config/db.php";
require_once "helpers.php";

$admin_id = $_GET["admin_id"] ?? null;
if (!$admin_id) {
    respond_error("Missing admin_id parameter.");
}
require_admin($pdo, (int) $admin_id);

$keyword = trim($_GET["keyword"] ?? "");
$filter = strtolower(trim($_GET["filter"] ?? "all"));

$sql = "
    SELECT r.rating_id, r.errand_id, r.rating_score, r.feedback, r.created_at,
           e.title AS errand_title,
           rated.full_name AS rated_user_name,
           reviewer.full_name AS rated_by_name
    FROM ratings r
    JOIN errands e ON r.errand_id = e.errand_id
    JOIN users rated ON r.rated_user_id = rated.user_id
    JOIN users reviewer ON r.rated_by_user_id = reviewer.user_id
    WHERE 1 = 1
";
$params = [];

switch ($filter) {
    case "5 stars":
        $sql .= " AND r.rating_score = 5";
        break;
    case "4 stars":
        $sql .= " AND r.rating_score = 4";
        break;
    case "3 or below":
        $sql .= " AND r.rating_score <= 3";
        break;
}

if ($keyword !== "") {
    $sql .= " AND (rated.full_name LIKE ? OR reviewer.full_name LIKE ? OR e.title LIKE ? OR r.feedback LIKE ?)";
    $like = "%" . $keyword . "%";
    array_push($params, $like, $like, $like, $like);
}

$sql .= " ORDER BY r.created_at DESC";
$stmt = $pdo->prepare($sql);
$stmt->execute($params);

respond_success("Ratings retrieved.", $stmt->fetchAll(PDO::FETCH_ASSOC));
?>
