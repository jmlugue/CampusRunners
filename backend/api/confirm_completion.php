<?php
require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();
require_fields($data, ["errand_id", "requester_id"]);

$errand_id = require_positive_int($data["errand_id"], "Errand ID");
$requester_id = require_positive_int($data["requester_id"], "Requester ID");
$errand = get_errand_by_id($pdo, $errand_id);

if (!$errand) {
    respond_error("Errand not found.");
}

if ((int) $errand["requester_id"] !== $requester_id) {
    respond_error("Only the requester can confirm completion.");
}

if ($errand["status"] !== "Completed by Helper") {
    respond_error("The helper must mark the errand completed first.");
}

update_errand_status($pdo, $errand_id, $requester_id, "Confirmed by Requester", "Requester confirmed completion.");
respond_success("Completion confirmed.");
?>
