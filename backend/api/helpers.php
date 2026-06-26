<?php
// backend/api/helpers.php

function read_json_input() {
    $input = file_get_contents("php://input");
    $data = json_decode($input, true);
    return is_array($data) ? $data : [];
}

function respond_success($message, $data = []) {
    echo json_encode([
        "success" => true,
        "message" => $message,
        "data" => $data
    ]);
    exit;
}

function respond_error($message) {
    echo json_encode([
        "success" => false,
        "message" => $message
    ]);
    exit;
}

function require_fields($data, $fields) {
    foreach ($fields as $field) {
        if (!isset($data[$field]) || trim((string) $data[$field]) === "") {
            respond_error("Missing required field: " . $field);
        }
    }
}

function sanitize_user($user) {
    if (!$user) {
        return null;
    }

    unset($user["password_hash"]);
    return $user;
}

function get_user_by_id($pdo, $user_id) {
    $stmt = $pdo->prepare("SELECT * FROM users WHERE user_id = ? LIMIT 1");
    $stmt->execute([$user_id]);
    return $stmt->fetch(PDO::FETCH_ASSOC);
}

function require_admin($pdo, $admin_id) {
    $admin = get_user_by_id($pdo, $admin_id);
    if (!$admin || $admin["role"] !== "admin" || $admin["account_status"] !== "active") {
        respond_error("Admin access is required.");
    }
    return $admin;
}

function get_errand_by_id($pdo, $errand_id) {
    $stmt = $pdo->prepare("SELECT * FROM errands WHERE errand_id = ? LIMIT 1");
    $stmt->execute([$errand_id]);
    return $stmt->fetch(PDO::FETCH_ASSOC);
}

function log_status_change($pdo, $errand_id, $changed_by, $old_status, $new_status, $reason = null) {
    $stmt = $pdo->prepare("
        INSERT INTO errand_status_logs (errand_id, changed_by, old_status, new_status, reason)
        VALUES (?, ?, ?, ?, ?)
    ");
    $stmt->execute([$errand_id, $changed_by, $old_status, $new_status, $reason]);
}

function update_errand_status($pdo, $errand_id, $changed_by, $new_status, $reason = null) {
    $errand = get_errand_by_id($pdo, $errand_id);
    if (!$errand) {
        respond_error("Errand not found.");
    }

    $stmt = $pdo->prepare("UPDATE errands SET status = ? WHERE errand_id = ?");
    $stmt->execute([$new_status, $errand_id]);
    log_status_change($pdo, $errand_id, $changed_by, $errand["status"], $new_status, $reason);
}

function user_can_access_errand($errand, $user_id) {
    return $errand && ((int) $errand["requester_id"] === (int) $user_id || (int) $errand["selected_helper_id"] === (int) $user_id);
}

function moderate_errand($title, $description, $category = "", $pickup_location = "", $dropoff_location = "") {
    $banned_terms = [
        "alcohol", "cigarette", "vape", "drug", "medicine", "supplement", "weapon",
        "exam answer", "quiz answer", "answer sheet", "borrow id", "id borrowing",
        "confidential", "grade record", "restricted area", "illegal", "dangerous",
        "prank", "harassment", "threat", "prohibited substance", "private record"
    ];

    $combined = strtolower($title . " " . $description . " " . $category . " " . $pickup_location . " " . $dropoff_location);
    $matched = [];

    foreach ($banned_terms as $term) {
        if (strpos($combined, strtolower($term)) !== false) {
            $matched[] = $term;
        }
    }

    if (count($matched) > 0) {
        return [
            "result" => "flagged",
            "matched_terms" => implode(", ", array_unique($matched))
        ];
    }

    return [
        "result" => "allowed",
        "matched_terms" => null
    ];
}
?>
