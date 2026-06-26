<?php
require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();
require_fields($data, ["admin_id", "target_user_id"]);

$admin_id = (int) $data["admin_id"];
$target_user_id = (int) $data["target_user_id"];
$account_status = trim($data["account_status"] ?? "");
$verification_status = trim($data["verification_status"] ?? "");
$details = trim($data["details"] ?? "");

require_admin($pdo, $admin_id);

$allowed_accounts = ["active", "deactivated"];
$allowed_verifications = ["pending", "verified", "rejected", "restricted"];

$sets = [];
$params = [];

if ($account_status !== "") {
    if (!in_array($account_status, $allowed_accounts)) {
        respond_error("Invalid account status.");
    }
    $sets[] = "account_status = ?";
    $params[] = $account_status;
}

if ($verification_status !== "") {
    if (!in_array($verification_status, $allowed_verifications)) {
        respond_error("Invalid verification status.");
    }
    $sets[] = "verification_status = ?";
    $params[] = $verification_status;
}

if (count($sets) === 0) {
    respond_error("No status update was provided.");
}

$params[] = $target_user_id;
$stmt = $pdo->prepare("UPDATE users SET " . implode(", ", $sets) . " WHERE user_id = ?");
$stmt->execute($params);

$action = $pdo->prepare("
    INSERT INTO admin_actions (admin_id, target_user_id, action_type, action_details)
    VALUES (?, ?, 'update_user_status', ?)
");
$action->execute([$admin_id, $target_user_id, $details]);

respond_success("User status updated.");
?>
