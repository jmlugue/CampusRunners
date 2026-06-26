<?php
require_once "../config/db.php";
require_once "helpers.php";

$admin_id = $_GET["admin_id"] ?? null;
if ($admin_id) {
    require_admin($pdo, $admin_id);
}

$keyword = trim($_GET["keyword"] ?? "");
$sql = "
    SELECT user_id, full_name, school_email, student_number, role, verification_status,
           account_status, average_rating, completed_errands, created_at
    FROM users
";
$params = [];

if ($keyword !== "") {
    $sql .= " WHERE full_name LIKE ? OR school_email LIKE ? OR student_number LIKE ?";
    $like = "%" . $keyword . "%";
    $params = [$like, $like, $like];
}

$sql .= " ORDER BY created_at DESC";
$stmt = $pdo->prepare($sql);
$stmt->execute($params);

respond_success("Users retrieved.", $stmt->fetchAll(PDO::FETCH_ASSOC));
?>
