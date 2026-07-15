<?php
require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();
require_fields($data, ["errand_id", "user_id"]);

$errand_id = require_positive_int($data["errand_id"], "Errand ID");
$user_id = require_positive_int($data["user_id"], "User ID");
$reason = validate_optional_text_length($data["reason"] ?? "", "Cancellation reason", 500);
$errand = get_errand_by_id($pdo, $errand_id);

if (!$errand) {
    respond_error("Errand not found.");
}

$is_requester = (int) $errand["requester_id"] === $user_id;
$is_helper = (int) $errand["selected_helper_id"] === $user_id;

if (!$is_requester && !$is_helper) {
    respond_error("You cannot cancel this errand.");
}

if (in_array($errand["status"], ["Assigned", "Accepted", "In Progress"], true) && $reason === "") {
    respond_error("A cancellation reason is required once a helper is involved.");
}

if ($is_requester && in_array($errand["status"], ["Open", "Has Applicants", "Assigned", "Accepted"])) {
    update_errand_status($pdo, $errand_id, $user_id, "Cancelled by Requester", $reason);
    respond_success("Errand cancelled by requester.");
}

if ($is_helper && in_array($errand["status"], ["Assigned", "Accepted"])) {
    update_errand_status($pdo, $errand_id, $user_id, "Cancelled by Helper", $reason);
    respond_success("Errand cancelled by helper.");
}

if ($errand["status"] === "In Progress") {
    update_errand_status($pdo, $errand_id, $user_id, $is_requester ? "Cancelled by Requester" : "Cancelled by Helper", $reason);
    respond_success("In-progress cancellation recorded.");
}

respond_error("This errand cannot be cancelled in its current status.");
?>
