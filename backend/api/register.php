<?php

require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();

$full_name = trim(
    $data["full_name"] ?? ""
);

$school_email = strtolower(
    trim($data["school_email"] ?? "")
);

$student_number = trim(
    $data["student_number"] ?? ""
);

$password = $data["password"] ?? "";

if (
    $full_name === "" ||
    $school_email === "" ||
    $student_number === "" ||
    $password === ""
) {
    respond_error("All fields are required.");
}

/*
 * Allows letters, spaces, apostrophes, and hyphens.
 * The name must begin and end with a letter.
 */
if (
    !preg_match(
        "/^\p{L}+(?:[ '\-]\p{L}+)*$/u",
        $full_name
    )
) {
    respond_error(
        "Full name may only contain letters, spaces, " .
        "apostrophes, and hyphens."
    );
}

if (strlen($full_name) > 100) {
    respond_error("Full name is too long.");
}

if (
    !filter_var(
        $school_email,
        FILTER_VALIDATE_EMAIL
    )
) {
    respond_error("Invalid school email format.");
}

/*
 * The email must:
 * - use @live.mcl.edu.ph
 * - begin with a letter or number
 * - end its username with a letter or number
 */
$email_pattern =
    "/^[a-z0-9]" .
    "(?:[a-z0-9._-]*[a-z0-9])?" .
    "@live\.mcl\.edu\.ph$/i";

if (
    !preg_match(
        $email_pattern,
        $school_email
    )
) {
    respond_error(
        "Use your official @live.mcl.edu.ph " .
        "student email."
    );
}

if (
    !preg_match(
        "/^\d{10}$/",
        $student_number
    )
) {
    respond_error(
        "Student number must contain exactly 10 digits."
    );
}

$password_length = function_exists("mb_strlen")
    ? mb_strlen($password, "UTF-8")
    : strlen($password);

if (
    $password_length < 8 ||
    $password_length > 64
) {
    respond_error(
        "Password must contain 8 to 64 characters."
    );
}

if (
    !preg_match(
        "/^[\p{L}\p{N}]/u",
        $password
    )
) {
    respond_error(
        "Password must begin with a letter or number."
    );
}

if (
    !preg_match(
        "/\p{L}/u",
        $password
    )
) {
    respond_error(
        "Password must contain at least one letter."
    );
}

if (
    !preg_match(
        "/\p{N}/u",
        $password
    )
) {
    respond_error(
        "Password must contain at least one number."
    );
}

if ($password !== trim($password)) {
    respond_error(
        "Password cannot begin or end with a space."
    );
}

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
            'verified',
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
        "Registration successful. You can now sign in.",
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