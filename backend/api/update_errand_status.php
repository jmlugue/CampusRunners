<?php
require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();
require_fields($data, ["errand_id", "changed_by", "new_status"]);

$errand_id = (int) $data["errand_id"];
$changed_by = (int) $data["changed_by"];
$new_status = trim($data["new_status"]);
$reason = trim($data["reason"] ?? "");

$errand = get_errand_by_id($pdo, $errand_id);
if (!$errand) {
    respond_error("Errand not found.");
}

if (!user_can_access_errand($errand, $changed_by)) {
    respond_error("You are not allowed to update this errand.");
}

$allowed = [
    "Accepted" => ["In Progress"],
    "In Progress" => ["Completed by Helper"],
    "Completed by Helper" => ["Confirmed by Requester"]
];

if (!isset($allowed[$errand["status"]]) || !in_array($new_status, $allowed[$errand["status"]])) {
    respond_error("Invalid status transition.");
}

if ($new_status === "In Progress" || $new_status === "Completed by Helper") {
    if ((int) $errand["selected_helper_id"] !== $changed_by) {
        respond_error("Only the selected helper can make this status update.");
    }
}

if ($new_status === "Confirmed by Requester" && (int) $errand["requester_id"] !== $changed_by) {
    respond_error("Only the requester can confirm completion.");
}

update_errand_status($pdo, $errand_id, $changed_by, $new_status, $reason);
respond_success("Errand status updated.");
?>
