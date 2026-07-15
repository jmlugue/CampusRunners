<?php
require_once "../config/db.php";
require_once "helpers.php";

$admin_id = $_GET["admin_id"] ?? null;
if (!$admin_id) {
    respond_error("Missing admin_id parameter.");
}
require_admin($pdo, (int) $admin_id);

$filter = strtolower(trim($_GET["filter"] ?? ($_GET["status"] ?? "all")));
$keyword = trim($_GET["keyword"] ?? "");

$sql = "
    SELECT e.*, requester.full_name AS requester_name, helper.full_name AS helper_name
    FROM errands e
    JOIN users requester ON e.requester_id = requester.user_id
    LEFT JOIN users helper ON e.selected_helper_id = helper.user_id
    WHERE 1 = 1
";
$params = [];

switch ($filter) {
    case "open":
        $sql .= " AND e.status IN ('Open', 'Has Applicants')";
        break;
    case "active":
        $sql .= " AND e.status IN ('Assigned', 'Accepted', 'In Progress', 'Completed by Helper')";
        break;
    case "completed":
        $sql .= " AND e.status IN ('Confirmed by Requester', 'Rated', 'Closed')";
        break;
    case "cancelled":
        $sql .= " AND e.status IN ('Cancelled by Requester', 'Cancelled by Helper')";
        break;
    case "reported":
            $sql .= "
            AND EXISTS (
                SELECT 1
                FROM reports r
                WHERE r.errand_id = e.errand_id
                AND r.status IN (
                    'pending',
                    'under_review'
                )
            )
        ";
        break;
    case "flagged":
        $sql .= " AND e.moderation_status = 'flagged'";
        break;
    case "rated":
        $sql .= " AND e.status = 'Rated'";
        break;
    case "closed":
        $sql .= " AND e.status = 'Closed'";
        break;
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
