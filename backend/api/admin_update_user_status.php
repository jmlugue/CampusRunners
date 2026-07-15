<?php
require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();
require_fields($data, ["admin_id", "target_user_id"]);

$admin_id = require_positive_int($data["admin_id"], "Admin ID");
$target_user_id = require_positive_int($data["target_user_id"], "Target user ID");
$account_status = trim($data["account_status"] ?? "");
$verification_status = trim($data["verification_status"] ?? "");
$details = validate_optional_text_length($data["details"] ?? "", "Action details", 500);

require_admin($pdo, $admin_id);

if ($admin_id === $target_user_id) {
    respond_error("Admins cannot update their own status from this action.");
}

if (!get_user_by_id($pdo, $target_user_id)) {
    respond_error("Target user was not found.");
}

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
