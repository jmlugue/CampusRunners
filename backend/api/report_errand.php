<?php

require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();

require_fields(
    $data,
    [
        "errand_id",
        "reported_by_user_id",
        "reason"
    ]
);

$errand_id = (int) $data["errand_id"];
$reported_by_user_id =
    (int) $data["reported_by_user_id"];

$reason = trim($data["reason"]);
$details = trim($data["details"] ?? "");

/*
 * Validate the reporter.
 */
$reporter = get_user_by_id(
    $pdo,
    $reported_by_user_id
);

if (!$reporter) {
    respond_error("Reporter account was not found.");
}

if ($reporter["account_status"] !== "active") {
    respond_error("This account cannot submit reports.");
}

/*
 * Validate the errand.
 */
$errand = get_errand_by_id(
    $pdo,
    $errand_id
);

if (!$errand) {
    respond_error("Errand not found.");
}

/*
 * A requester should not report their own errand.
 */
if (
    (int) $errand["requester_id"] ===
    $reported_by_user_id
) {
    respond_error(
        "You cannot report an errand that you requested."
    );
}

/*
 * Removed errands should no longer accept new reports.
 */
if ($errand["status"] === "Removed by Admin") {
    respond_error(
        "This errand has already been removed."
    );
}

/*
 * Prevent the same student from creating multiple
 * pending reports for the same errand.
 */
$duplicate = $pdo->prepare("
    SELECT report_id
    FROM reports
    WHERE errand_id = ?
      AND reported_by_user_id = ?
      AND status IN ('pending', 'under_review')
    LIMIT 1
");

$duplicate->execute([
    $errand_id,
    $reported_by_user_id
]);

if ($duplicate->fetch(PDO::FETCH_ASSOC)) {
    respond_error(
        "You already have an active report for this errand."
    );
}

try {
    /*
     * The report is saved separately.
     *
     * Do not change errands.status here.
     */
    $stmt = $pdo->prepare("
        INSERT INTO reports (
            errand_id,
            reported_by_user_id,
            report_type,
            reason,
            details
        )
        VALUES (?, ?, 'errand', ?, ?)
    ");

    $stmt->execute([
        $errand_id,
        $reported_by_user_id,
        $reason,
        $details
    ]);

    $report_id = (int) $pdo->lastInsertId();

    respond_success(
        "Errand report submitted for admin review.",
        [
            "report_id" => $report_id,
            "errand_status" => $errand["status"]
        ]
    );
} catch (PDOException $e) {
    respond_error(
        "The report could not be submitted."
    );
}
?>