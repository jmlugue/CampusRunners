<?php
require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();
require_fields($data, ["admin_id", "errand_id"]);

$admin_id = require_positive_int($data["admin_id"], "Admin ID");
$errand_id = require_positive_int($data["errand_id"], "Errand ID");
$reason = validate_optional_text_length($data["reason"] ?? "Removed by admin.", "Removal reason", 500);

if ($reason === "") {
    $reason = "Removed by admin.";
}

require_admin($pdo, $admin_id);
$errand = get_errand_by_id($pdo, $errand_id);
if (!$errand) {
    respond_error("Errand not found.");
}

update_errand_status($pdo, $errand_id, $admin_id, "Removed by Admin", $reason);

$stmt = $pdo->prepare("
    INSERT INTO admin_actions (admin_id, target_errand_id, action_type, action_details)
    VALUES (?, ?, 'remove_errand', ?)
");
$stmt->execute([$admin_id, $errand_id, $reason]);

respond_success("Errand removed by admin.");
?>
