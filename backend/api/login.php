<?php

require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();

$login_type = strtolower(
    trim($data["login_type"] ?? "student")
);

$password = $data["password"] ?? "";

if ($password === "") {
    respond_error("Password is required.");
}

if ($login_type === "student") {
    $student_number = trim(
        $data["student_number"] ?? ""
    );

    if (!preg_match('/^\d{10}$/', $student_number)) {
        respond_error(
            "Student number must contain exactly 10 digits."
        );
    }

    $statement = $pdo->prepare("
        SELECT *
        FROM users
        WHERE student_number = ?
          AND role = 'student'
        LIMIT 1
    ");

    $statement->execute([
        $student_number
    ]);

    $invalid_message =
        "Invalid student number or password.";

} elseif ($login_type === "admin") {
    $school_email = strtolower(
        trim($data["school_email"] ?? "")
    );

    if (
        !filter_var(
            $school_email,
            FILTER_VALIDATE_EMAIL
        )
    ) {
        respond_error(
            "Enter a valid administrator email."
        );
    }

    $statement = $pdo->prepare("
        SELECT *
        FROM users
        WHERE school_email = ?
          AND role = 'admin'
        LIMIT 1
    ");

    $statement->execute([
        $school_email
    ]);

    $invalid_message =
        "Invalid administrator email or password.";

} else {
    respond_error("Invalid login type.");
}

$user = $statement->fetch(
    PDO::FETCH_ASSOC
);

if (
    !$user ||
    !password_verify(
        $password,
        $user["password_hash"]
    )
) {
    respond_error($invalid_message);
}

if ($user["account_status"] !== "active") {
    respond_error(
        "This account is not active."
    );
}

if (
    $user["verification_status"] !== "verified"
) {
    respond_error(
        "This account has not been verified."
    );
}

unset($user["password_hash"]);

respond_success(
    "Login successful.",
    $user
);

?>