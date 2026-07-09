# API_DOCUMENTATION.md

## Base URL

Example for local XAMPP testing:

```text
http://10.0.2.2/IT140P-MP-MalayanQuest/backend/api/
```

For Android phone testing, replace `10.0.2.2` with the local IP address of the computer running XAMPP.

## Standard Success Response

```json
{
  "success": true,
  "message": "Action completed successfully",
  "data": {}
}
```

## Standard Error Response

```json
{
  "success": false,
  "message": "Error message here"
}
```

## Authentication Endpoints

### POST register.php

Registers a new student account.

Expected fields:

```json
{
  "full_name": "Juan Dela Cruz",
  "school_email": "juan@example.mcl.edu.ph",
  "student_number": "2026123456",
  "password": "password"
}
```

### POST login.php

Logs in a student or admin.

Expected fields:

```json
{
  "school_email": "admin@mcl.edu.ph",
  "password": "password"
}
```

## Errand Endpoints

### POST create_errand.php

Creates a new errand after moderation.

Expected fields:

```json
{
  "requester_id": 2,
  "title": "Buy bluebook from bookstore",
  "description": "Please buy one bluebook from the bookstore before my quiz.",
  "category": "Bluebook Purchase",
  "pickup_location": "Bookstore",
  "dropoff_location": "Room A301",
  "deadline": "2026-07-15 13:00:00",
  "reward_amount": 20,
  "reward_note": "Cash after delivery"
}
```

### GET get_available_errands.php

Returns open errands that helpers can browse.

Optional filters:

```text
category
location
keyword
```

### POST apply_to_errand.php

Helper applies to an errand.

Expected fields:

```json
{
  "errand_id": 1,
  "helper_id": 3,
  "offer_note": "I can buy this before 12:30 PM.",
  "estimated_completion_time": "30 minutes"
}
```

### POST select_helper.php

Requester selects one helper.

Expected fields:

```json
{
  "errand_id": 1,
  "requester_id": 2,
  "application_id": 1
}
```

### POST update_errand_status.php

Updates errand status.

Expected fields:

```json
{
  "errand_id": 1,
  "changed_by": 3,
  "new_status": "In Progress",
  "reason": ""
}
```

## Message Endpoints

### POST send_message.php

Sends a message connected to an errand.

Expected fields:

```json
{
  "errand_id": 1,
  "sender_id": 2,
  "receiver_id": 3,
  "message_text": "Where are you now?"
}
```

### GET get_messages.php

Returns messages for an errand.

Required query parameters:

```text
errand_id
user_id
```

## Rating Endpoints

### POST submit_rating.php

Requester rates the helper after completion.

Expected fields:

```json
{
  "errand_id": 1,
  "rated_user_id": 3,
  "rated_by_user_id": 2,
  "rating_score": 5,
  "feedback": "Fast and reliable."
}
```

## Report Endpoints

### POST report_errand.php

Reports an unsafe or inappropriate errand.

### POST report_user.php

Reports a user.

## Admin Endpoints

1. admin_dashboard.php
2. admin_get_users.php
3. admin_update_user_status.php
4. admin_get_errands.php
5. admin_remove_errand.php
6. admin_get_reports.php
7. admin_resolve_report.php
8. admin_get_flagged_errands.php
