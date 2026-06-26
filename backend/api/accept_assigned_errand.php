<?php
require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();
require_fields($data, ["errand_id", "helper_id"]);

$errand_id = (int) $data["errand_id"];
$helper_id = (int) $data["helper_id"];
$errand = get_errand_by_id($pdo, $errand_id);

if (!$errand) {
    respond_error("Errand not found.");
}

if ((int) $errand["selected_helper_id"] !== $helper_id) {
    respond_error("Only the selected helper can accept this errand.");
}

if ($errand["status"] !== "Assigned") {
    respond_error("This errand is not waiting for helper acceptance.");
}

update_errand_status($pdo, $errand_id, $helper_id, "Accepted", "Selected helper accepted the assignment.");
respond_success("Errand accepted.");
?>
