<?php
require_once "../config/db.php";
require_once "helpers.php";

$admin_id = $_GET["admin_id"] ?? null;
if (!$admin_id) {
    respond_error("Missing admin_id parameter.");
}
require_admin($pdo, (int) $admin_id);

$keyword = trim($_GET["keyword"] ?? "");
$filter = strtolower(str_replace(" ", "_", trim($_GET["filter"] ?? "all")));
$sql = "
    SELECT user_id, full_name, school_email, student_number, role, verification_status,
           account_status, average_rating, completed_errands, created_at
    FROM users
    WHERE 1 = 1
";
$params = [];

if ($keyword !== "") {
    $sql .= " AND (full_name LIKE ? OR school_email LIKE ? OR student_number LIKE ?)";
    $like = "%" . $keyword . "%";
    $params = [$like, $like, $like];
}

switch ($filter) {
    case "pending":
        $sql .= " AND role = 'student' AND verification_status = 'pending' AND account_status = 'active'";
        break;
    case "verified":
        $sql .= " AND role = 'student' AND verification_status = 'verified' AND account_status = 'active'";
        break;
    case "restricted":
        $sql .= " AND role = 'student' AND verification_status = 'restricted' AND account_status = 'active'";
        break;
    case "rejected":
        $sql .= " AND role = 'student' AND verification_status = 'rejected' AND account_status = 'active'";
        break;
    case "deactivated":
        $sql .= " AND role = 'student' AND account_status = 'deactivated'";
        break;
}

$sql .= " ORDER BY created_at DESC";
$stmt = $pdo->prepare($sql);
$stmt->execute($params);

respond_success("Users retrieved.", $stmt->fetchAll(PDO::FETCH_ASSOC));
?>
