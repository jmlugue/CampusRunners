<?php
// backend/api/helpers.php

function read_json_input() {
    $input = file_get_contents("php://input");
    return json_decode($input, true);
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

function moderate_errand($title, $description) {
    $banned_terms = [
        "alcohol", "cigarette", "vape", "drugs", "medicine", "weapon",
        "exam answer", "quiz answer", "answer sheet", "borrow id", "id borrowing",
        "confidential", "grade record", "restricted area", "illegal", "dangerous",
        "prank", "harassment"
    ];

    $combined = strtolower($title . " " . $description);
    $matched = [];

    foreach ($banned_terms as $term) {
        if (strpos($combined, strtolower($term)) !== false) {
            $matched[] = $term;
        }
    }

    if (count($matched) > 0) {
        return [
            "result" => "flagged",
            "matched_terms" => implode(", ", $matched)
        ];
    }

    return [
        "result" => "allowed",
        "matched_terms" => null
    ];
}
?>
