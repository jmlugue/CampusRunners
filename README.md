# IT140P-MP-MalayanQuest

## App Name

Malayan Quest

## Description

Malayan Quest is a REST-based Android mobile application for Mapúa Malayan Colleges Laguna students. It allows verified students to post small campus-based errands, apply as helpers, select helpers, track progress, message each other, confirm completion, submit ratings, and report unsafe activity.

## Technology Stack

- Android Studio
- Java
- XML layouts
- PHP REST API
- MySQL
- XAMPP
- JSON request and response

## Why PHP + MySQL

The project uses PHP and MySQL instead of Supabase or Firebase because the final project submission requires a database file for the RESTful approach. MySQL allows the group to export and submit an SQL file.

## Main Features

1. Student registration and login
2. School email and student number verification fields
3. Post errand
4. Browse available errands
5. Apply as helper
6. Requester selects from multiple helpers
7. Errand status tracking
8. In-app messaging
9. Completion confirmation
10. Ratings and feedback
11. Report unsafe errands or users
12. Admin monitoring dashboard
13. Admin user restriction and errand moderation

## Suggested Setup

For detailed local setup, see `docs/LOCAL_SETUP.md`. For a groupmate-friendly setup checklist and AI assistant handoff prompt, see `docs/GROUPMATE_AI_SETUP_GUIDE.md`.

### Backend

1. Install XAMPP.
2. Start Apache and MySQL.
3. Copy the `backend` folder to your XAMPP `htdocs` folder.
4. Create a database named `malayanquest_db` in phpMyAdmin.
5. Import `database/malayanquest_db.sql`.
6. Update database credentials in `backend/config/db.php`.

### Android

1. Open the Android project in Android Studio.
2. Set the default API base URL in `android/app/src/main/java/com/malayanquest/app/Config.kt`.
3. For emulator testing, use `http://10.0.2.2:8080/IT140P-MP-MalayanQuest/backend/api/` if Apache runs on port `8080`, or remove `:8080` for port `80`.
4. For phone testing, replace `10.0.2.2` with the computer IPv4 address.

## Sample Test Accounts

For prototype testing, create or use sample accounts from the database seed file.

Admin:

- Email: admin@mcl.edu.ph
- Password: password

Student sample accounts should be changed before final demonstration.

## Important Notes

- The app does not process real payments.
- The app may record an optional reward amount or reward note.
- Errands must be school-related, safe, and limited to campus or near-campus locations.
- Student numbers and school emails should not be publicly displayed.

## Group Members

Add group member details here:

1. Member 1:
2. Member 2:
3. Member 3:
4. Member 4:
