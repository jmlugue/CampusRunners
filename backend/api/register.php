<?php

require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();

if (
    trim($data["full_name"] ?? "") === "" ||
    trim($data["school_email"] ?? "") === "" ||
    trim($data["student_number"] ?? "") === "" ||
    ($data["password"] ?? "") === ""
) {
    respond_error("All fields are required.");
}

$full_name = validate_full_name($data["full_name"]);
$school_email = validate_school_email($data["school_email"]);
$student_number = validate_student_number($data["student_number"]);
$password = $data["password"];
validate_password_rules($password);

$password_hash = password_hash(
    $password,
    PASSWORD_DEFAULT
);

try {
    $statement = $pdo->prepare("
        INSERT INTO users (
            full_name,
            school_email,
            student_number,
            password_hash,
            role,
            verification_status,
            account_status
        )
        VALUES (
            ?,
            ?,
            ?,
            ?,
            'student',
            'pending',
            'active'
        )
    ");

    $statement->execute([
        $full_name,
        $school_email,
        $student_number,
        $password_hash
    ]);

    respond_success(
        "Registration successful. Please wait for admin verification before signing in.",
        [
            "user_id" => $pdo->lastInsertId()
        ]
    );

} catch (PDOException $exception) {
    respond_error(
        "Registration failed. The email or student " .
        "number may already be registered."
    );
}

?>
