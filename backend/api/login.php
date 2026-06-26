<?php
require_once "../config/db.php";
require_once "helpers.php";

$data = read_json_input();

$school_email = trim($data["school_email"] ?? "");
$password = $data["password"] ?? "";

if ($school_email === "" || $password === "") {
    respond_error("School email and password are required.");
}

$stmt = $pdo->prepare("SELECT * FROM users WHERE school_email = ? LIMIT 1");
$stmt->execute([$school_email]);
$user = $stmt->fetch(PDO::FETCH_ASSOC);

if (!$user) {
    respond_error("Invalid email or password.");
}

if ($user["account_status"] !== "active") {
    respond_error("This account is not active.");
}

if (!password_verify($password, $user["password_hash"])) {
    respond_error("Invalid email or password.");
}

unset($user["password_hash"]);
respond_success("Login successful.", $user);
?>
