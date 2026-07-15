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

function require_positive_int($value, $field_name) {
    if (!is_numeric($value) || (int) $value <= 0) {
        respond_error($field_name . " must be a valid positive number.");
    }

    return (int) $value;
}

function require_text_length($value, $field_name, $min, $max) {
    $text = trim((string) $value);
    $length = function_exists("mb_strlen")
        ? mb_strlen($text, "UTF-8")
        : strlen($text);

    if ($length < $min) {
        respond_error($field_name . " must contain at least " . $min . " characters.");
    }

    if ($length > $max) {
        respond_error($field_name . " must not exceed " . $max . " characters.");
    }

    return $text;
}

function validate_optional_text_length($value, $field_name, $max) {
    $text = trim((string) $value);
    $length = function_exists("mb_strlen")
        ? mb_strlen($text, "UTF-8")
        : strlen($text);

    if ($length > $max) {
        respond_error($field_name . " must not exceed " . $max . " characters.");
    }

    return $text;
}

function validate_full_name($full_name) {
    $full_name = require_text_length($full_name, "Full name", 2, 100);

    if (!preg_match("/^\p{L}+(?:[ '\-]\p{L}+)*$/u", $full_name)) {
        respond_error(
            "Full name may only contain letters, spaces, apostrophes, and hyphens."
        );
    }

    return $full_name;
}

function validate_school_email($school_email) {
    $school_email = strtolower(trim((string) $school_email));

    if (!filter_var($school_email, FILTER_VALIDATE_EMAIL)) {
        respond_error("Invalid school email format.");
    }

    if (!preg_match("/^[a-z0-9](?:[a-z0-9._-]*[a-z0-9])?@(?:live\.)?mcl\.edu\.ph$/i", $school_email)) {
        respond_error(
            "Use your official @mcl.edu.ph or @live.mcl.edu.ph student email."
        );
    }

    return $school_email;
}

function validate_student_number($student_number) {
    $student_number = trim((string) $student_number);

    if (!preg_match("/^\d{10}$/", $student_number)) {
        respond_error("Student number must contain exactly 10 digits.");
    }

    return $student_number;
}

function validate_password_rules($password) {
    $length = function_exists("mb_strlen")
        ? mb_strlen($password, "UTF-8")
        : strlen($password);

    if ($length < 8 || $length > 64) {
        respond_error("Password must contain 8 to 64 characters.");
    }

    if (!preg_match("/^[\p{L}\p{N}]/u", $password)) {
        respond_error("Password must begin with a letter or number.");
    }

    if (!preg_match("/\p{L}/u", $password)) {
        respond_error("Password must contain at least one letter.");
    }

    if (!preg_match("/\p{N}/u", $password)) {
        respond_error("Password must contain at least one number.");
    }

    if ($password !== trim($password)) {
        respond_error("Password cannot begin or end with a space.");
    }
}

function validate_active_verified_student($pdo, $user_id, $field_name = "Student") {
    $user = get_user_by_id($pdo, $user_id);

    if (!$user || $user["role"] !== "student") {
        respond_error($field_name . " account was not found.");
    }

    if ($user["account_status"] !== "active") {
        respond_error($field_name . " account is not active.");
    }

    if ($user["verification_status"] !== "verified") {
        respond_error($field_name . " account is not verified.");
    }

    return $user;
}

function validate_errand_category($category) {
    $category = trim((string) $category);
    $allowed = ["Food", "Printing", "Bluebook", "Supplies", "Delivery", "Others"];

    if (!in_array($category, $allowed, true)) {
        respond_error("Choose a valid errand category.");
    }

    return $category;
}

function validate_deadline($deadline) {
    $deadline = trim((string) $deadline);
    $date = DateTime::createFromFormat("Y-m-d H:i:s", $deadline);
    $errors = DateTime::getLastErrors();

    if (!$date || ($errors !== false && ($errors["warning_count"] > 0 || $errors["error_count"] > 0))) {
        respond_error("Deadline must use YYYY-MM-DD HH:MM:SS format.");
    }

    if ($date <= new DateTime()) {
        respond_error("Deadline must be in the future.");
    }

    $max = new DateTime("+30 days");
    if ($date > $max) {
        respond_error("Deadline must be within the next 30 days.");
    }

    return $deadline;
}

function validate_reward_amount($reward_amount) {
    if ($reward_amount === null || trim((string) $reward_amount) === "") {
        return null;
    }

    if (!is_numeric($reward_amount)) {
        respond_error("Reward amount must be a number.");
    }

    $amount = (float) $reward_amount;
    if ($amount < 0 || $amount > 1000) {
        respond_error("Reward amount must be between 0 and 1000.");
    }

    return number_format($amount, 2, ".", "");
}

function validate_room_location($label, $location) {
    preg_match_all('/\b([REre])[- ]?(\d{3})\b/', $location, $matches, PREG_SET_ORDER);

    foreach ($matches as $match) {
        $room_number = (int) $match[2];
        if (!is_valid_mcl_room_number($room_number)) {
            respond_error($label . " room must be from R101-R113, E101-E113, up to R501-R513 or E501-E513.");
        }
    }

    $without_prefixed_rooms = preg_replace('/\b([REre])[- ]?(\d{3})\b/', '', $location);
    if (preg_match('/\b([1-5][0-9]{2})\b/', $without_prefixed_rooms)) {
        respond_error($label . " room code must start with R or E, example R101 or E413.");
    }
}

function is_valid_mcl_room_number($room_number) {
    $floor = intdiv($room_number, 100);
    $room = $room_number % 100;
    return $floor >= 1 && $floor <= 5 && $room >= 1 && $room <= 13;
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
