<?php
require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();

$full_name = trim($data["full_name"] ?? "");
$school_email = trim($data["school_email"] ?? "");
$student_number = trim($data["student_number"] ?? "");
$password = $data["password"] ?? "";

if ($full_name === "" || $school_email === "" || $student_number === "" || $password === "") {
    respond_error("All fields are required.");
}

if (!filter_var($school_email, FILTER_VALIDATE_EMAIL)) {
    respond_error("Invalid school email format.");
}

if (!preg_match("/@mcl\.edu\.ph$/i", $school_email)) {
    respond_error("Use your MCL school email address.");
}

$password_hash = password_hash($password, PASSWORD_DEFAULT);

try {
    $stmt = $pdo->prepare("
        INSERT INTO users (full_name, school_email, student_number, password_hash, role, verification_status, account_status)
        VALUES (?, ?, ?, ?, 'student', 'verified', 'active')
    ");
    $stmt->execute([$full_name, $school_email, $student_number, $password_hash]);

    respond_success("Registration successful.", [
        "user_id" => $pdo->lastInsertId()
    ]);
} catch (PDOException $e) {
    respond_error("Registration failed. Email or student number may already exist.");
}
?>
