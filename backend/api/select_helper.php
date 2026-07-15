<?php
require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();
require_fields($data, ["errand_id", "requester_id", "application_id"]);

$errand_id = require_positive_int($data["errand_id"], "Errand ID");
$requester_id = require_positive_int($data["requester_id"], "Requester ID");
$application_id = require_positive_int($data["application_id"], "Application ID");

$errand = get_errand_by_id($pdo, $errand_id);
if (!$errand) {
    respond_error("Errand not found.");
}

if ((int) $errand["requester_id"] !== $requester_id) {
    respond_error("Only the requester can select a helper.");
}

if (!in_array($errand["status"], ["Open", "Has Applicants"])) {
    respond_error("A helper can only be selected before the errand is assigned.");
}

$stmt = $pdo->prepare("
    SELECT * FROM errand_applications
    WHERE application_id = ? AND errand_id = ? AND status = 'pending'
    LIMIT 1
");
$stmt->execute([$application_id, $errand_id]);
$application = $stmt->fetch(PDO::FETCH_ASSOC);

if (!$application) {
    respond_error("Pending application not found.");
}

try {
    $pdo->beginTransaction();

    $pdo->prepare("UPDATE errand_applications SET status = 'not_selected' WHERE errand_id = ?")
        ->execute([$errand_id]);
    $pdo->prepare("UPDATE errand_applications SET status = 'selected' WHERE application_id = ?")
        ->execute([$application_id]);
    $pdo->prepare("UPDATE errands SET selected_helper_id = ?, status = 'Assigned' WHERE errand_id = ?")
        ->execute([$application["helper_id"], $errand_id]);

    log_status_change($pdo, $errand_id, $requester_id, $errand["status"], "Assigned", "Requester selected a helper.");

    $pdo->commit();
    respond_success("Helper selected.", [
        "helper_id" => $application["helper_id"]
    ]);
} catch (PDOException $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    respond_error("Failed to select helper.");
}
?>
