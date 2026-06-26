<?php
// backend/config/db.php
// Database configuration for IT140P-MP-CampusRunners

$host = "localhost";
$db_name = "campusrunners_db";
$username = "root";
$password = "";

header("Content-Type: application/json; charset=UTF-8");

try {
    $pdo = new PDO("mysql:host=$host;dbname=$db_name;charset=utf8mb4", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    echo json_encode([
        "success" => false,
        "message" => "Database connection failed."
    ]);
    exit;
}
?>
