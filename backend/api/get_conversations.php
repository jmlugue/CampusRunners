<?php
// backend/api/get_conversations.php
// Returns a list of active conversations for the user.
// Each conversation is an errand where the user is either the requester
// or the selected helper, along with the last message and unread count.
require_once "../config/db.php";
require_once "helpers.php";

$user_id = $_GET["user_id"] ?? null;

if (!$user_id) {
    respond_error("Missing user_id parameter.");
}

$user_id = (int) $user_id;

// Find errands where the user is requester or selected helper AND has messages
$stmt = $pdo->prepare("
    SELECT
        e.errand_id,
        e.title,
        e.status,
        e.requester_id,
        e.selected_helper_id,
        req.full_name AS requester_name,
        hlp.full_name AS helper_name,
        (SELECT m.message_text FROM messages m
         WHERE m.errand_id = e.errand_id
         ORDER BY m.created_at DESC LIMIT 1) AS last_message,
        (SELECT m.sender_id FROM messages m
         WHERE m.errand_id = e.errand_id
         ORDER BY m.created_at DESC LIMIT 1) AS last_sender_id,
        (SELECT m.created_at FROM messages m
         WHERE m.errand_id = e.errand_id
         ORDER BY m.created_at DESC LIMIT 1) AS last_message_time,
        (SELECT COUNT(*) FROM messages m
         WHERE m.errand_id = e.errand_id AND m.receiver_id = ? AND m.is_read = 0) AS unread_count
    FROM errands e
    JOIN users req ON e.requester_id = req.user_id
    LEFT JOIN users hlp ON e.selected_helper_id = hlp.user_id
    WHERE (e.requester_id = ? OR e.selected_helper_id = ?)
      AND e.selected_helper_id IS NOT NULL
      AND EXISTS (
          SELECT 1 FROM messages m WHERE m.errand_id = e.errand_id
      )
    ORDER BY last_message_time DESC
");
$stmt->execute([$user_id, $user_id, $user_id]);
$conversations = $stmt->fetchAll(PDO::FETCH_ASSOC);

// Also find errands with a selected helper but no messages yet
$stmt2 = $pdo->prepare("
    SELECT
        e.errand_id,
        e.title,
        e.status,
        e.requester_id,
        e.selected_helper_id,
        req.full_name AS requester_name,
        hlp.full_name AS helper_name,
        NULL AS last_message,
        NULL AS last_sender_id,
        e.updated_at AS last_message_time,
        0 AS unread_count
    FROM errands e
    JOIN users req ON e.requester_id = req.user_id
    LEFT JOIN users hlp ON e.selected_helper_id = hlp.user_id
    WHERE (e.requester_id = ? OR e.selected_helper_id = ?)
      AND e.selected_helper_id IS NOT NULL
      AND NOT EXISTS (
          SELECT 1 FROM messages m WHERE m.errand_id = e.errand_id
      )
      AND e.status NOT IN ('Closed', 'Cancelled by Requester', 'Cancelled by Helper', 'Removed by Admin')
    ORDER BY e.updated_at DESC
");
$stmt2->execute([$user_id, $user_id]);
$empty_convos = $stmt2->fetchAll(PDO::FETCH_ASSOC);

$all = array_merge($conversations, $empty_convos);

// Add computed fields
foreach ($all as &$convo) {
    $convo["other_name"] = ((int) $convo["requester_id"] === $user_id)
        ? ($convo["helper_name"] ?? "Helper")
        : ($convo["requester_name"] ?? "Requester");
    $convo["role_label"] = ((int) $convo["requester_id"] === $user_id) ? "Requester" : "Helper";
}

respond_success("Conversations loaded.", $all);
?>
