# CODEX_PROMPT.md

You are helping build a final project called IT140P-MP-MalayanQuest.

The app display name is Malayan Quest.

The project must be an Android Studio mobile application using a RESTful approach connected to a MySQL database through PHP REST API files. Do not use Firebase, Supabase, or other cloud databases as the main backend because the final submission requires a database file. Use PHP + MySQL so the database can be exported as an SQL file.

## Project Name

IT140P-MP-MalayanQuest

## App Display Name

Malayan Quest

## Project Concept

Malayan Quest is a school-based micro-errand request app for Mapúa Malayan Colleges Laguna students. Verified students can post small errands, apply as helpers, choose helpers, track errand status, message each other, confirm completion, rate helpers, and report unsafe activity. Admin users can monitor errands, reports, users, ratings, flagged content, and restrict users if needed.

## Target Users

1. Student Requester
2. Student Helper
3. Admin

A student can be both requester and helper.

## Technology Requirements

1. Android Studio mobile app
2. Java preferred
3. XML layouts preferred
4. PHP REST API backend
5. MySQL database
6. JSON request and response
7. XAMPP-compatible setup
8. Exportable SQL database file
9. No direct Android-to-MySQL connection
10. No real payment processing

## Required Folder Structure

Create this structure:

```text
IT140P-MP-MalayanQuest/
├── android/
│   └── Android Studio project files
├── backend/
│   ├── config/
│   │   └── db.php
│   └── api/
│       └── PHP REST API files
├── database/
│   └── malayanquest_db.sql
├── docs/
│   ├── README.md
│   ├── API_DOCUMENTATION.md
│   ├── DATABASE_GUIDE.md
│   ├── USER_GUIDE.md
│   └── EVALUATION_SUMMARY_TEMPLATE.md
├── evaluation/
│   └── GOOGLE_FORM_QUESTIONS.md
└── presentation_notes/
    └── PRESENTATION_FLOW.md
```

## Visual Theme

Use a clean blue campus-inspired theme.

Recommended colors:

```xml
<color name="primary_blue">#0B4EA2</color>
<color name="secondary_blue">#1976D2</color>
<color name="background_white">#FFFFFF</color>
<color name="card_light">#F4F8FC</color>
<color name="text_primary">#102A43</color>
<color name="text_secondary">#6B7280</color>
<color name="success_green">#2E7D32</color>
<color name="warning_amber">#F9A825</color>
<color name="danger_red">#D32F2F</color>
```

UI rules:

1. Use primary blue for app bars, headers, and primary buttons.
2. Use secondary blue for secondary actions and links.
3. Use white and light blue-gray for backgrounds and cards.
4. Use dark navy for main text.
5. Use gray for supporting text.
6. Use green, amber, and red only for status indicators.
7. Keep all screens clean, readable, and student-friendly.
8. Use cards for errand lists.
9. Use status badges for errand state.
10. Use confirmation dialogs before important actions.

## Core App Features

1. Register using full name, school email, student number, and password
2. Login
3. Student dashboard
4. Post errand
5. Automatic errand moderation using rule-based keyword checking
6. Browse available errands
7. Filter errands by category, location, deadline, and keyword
8. Apply as helper
9. Requester views multiple helper applicants
10. Requester selects one helper
11. Selected helper accepts assignment
12. Status tracking
13. In-app messaging between requester and selected helper
14. Helper marks errand as In Progress
15. Helper marks errand as Completed by Helper
16. Requester confirms completion
17. Requester rates helper
18. Requester or helper can report an errand or user
19. Admin dashboard
20. Admin can view users, errands, reports, flagged errands, and ratings
21. Admin can deactivate or restrict users
22. Admin can remove inappropriate errands
23. Admin can resolve reports

## Allowed Errand Categories

1. Food Pickup
2. Printing Pickup
3. Document Delivery
4. School Supplies Purchase
5. Bluebook Purchase
6. Bookstore Item Purchase
7. Campus Item Delivery
8. Classroom-to-Classroom Delivery
9. Library or Bookstore Errand
10. Nearby Establishment Errand
11. Other School-Related Errand

## Allowed Locations

1. Canteen
2. Library
3. Bookstore
4. Printing Services
5. Classrooms
6. School Buildings
7. Nearby Food Restaurants
8. Nearby Businesses
9. Other Near-MCL Location

## Banned Errands

1. Illegal items or illegal activities
2. Alcohol
3. Cigarettes
4. Vape products
5. Prohibited substances
6. Medicine or medical-related purchases
7. Weapons or dangerous objects
8. Confidential school documents
9. Exams, quizzes, answer sheets, grades, IDs, or private records
10. Tasks that must personally be done by the student
11. Activities that violate school rules
12. Requests involving harassment, threats, pranks, or unsafe behavior
13. Requests involving restricted areas
14. Tasks outside reasonable campus or near-campus scope

## Moderation Requirement

Create a PHP moderation function used before creating an errand.

For the prototype, use rule-based keyword checks. Check the title and description for banned words. If the errand is safe, allow posting. If suspicious, mark as Flagged or reject it. Save the moderation result in the moderation_logs table.

Make the moderation code modular so an AI moderation model or AI API can be integrated in the future, but do not require a paid external AI service for the prototype.

## Errand Status Flow

1. Open
2. Has Applicants
3. Assigned
4. Accepted
5. In Progress
6. Completed by Helper
7. Confirmed by Requester
8. Rated
9. Closed

Other statuses:

1. Cancelled by Requester
2. Cancelled by Helper
3. Reported
4. Removed by Admin
5. Expired
6. Flagged

## Cancellation Rules

1. Requester can cancel while errand is Open or Has Applicants.
2. Requester can cancel after assignment only if helper has not marked it In Progress.
3. Helper can withdraw before In Progress.
4. After In Progress, cancellation requires a reason and should be logged.
5. Admin can view cancellation history.

## Privacy Rules

1. Do not publicly display school email addresses.
2. Do not publicly display student numbers.
3. Public helper cards should show only name, average rating, completed errand count, and offer note.
4. Passwords must be hashed in PHP using password_hash.
5. Password verification must use password_verify.
6. Database credentials must be stored only in backend config files.
7. Android must communicate only with PHP API endpoints.
8. In-app messages should only be visible to the requester and selected helper.

## Database

Create a MySQL database named:

```sql
malayanquest_db
```

Create these MySQL tables:

1. users
2. locations
3. errands
4. errand_applications
5. errand_status_logs
6. messages
7. ratings
8. reports
9. moderation_logs
10. admin_actions

Suggested table fields are already included in database/malayanquest_db.sql. Use or improve that schema.

Create sample seed data:

1. One admin account
2. Five student accounts
3. Sample locations
4. Five sample errands
5. Sample applications
6. Sample ratings if needed

## REST API Endpoints to Create

Authentication:

1. register.php
2. login.php

Users:

1. get_user_profile.php
2. update_user_profile.php
3. get_user_history.php
4. get_helper_profile.php

Errands:

1. create_errand.php
2. get_available_errands.php
3. get_errand_details.php
4. apply_to_errand.php
5. get_errand_applicants.php
6. select_helper.php
7. accept_assigned_errand.php
8. update_errand_status.php
9. confirm_completion.php
10. cancel_errand.php
11. get_my_posted_errands.php
12. get_my_helper_errands.php

Ratings:

1. submit_rating.php
2. get_user_ratings.php

Messages:

1. send_message.php
2. get_messages.php
3. mark_messages_read.php

Reports:

1. report_errand.php
2. report_user.php

Admin:

1. admin_dashboard.php
2. admin_get_users.php
3. admin_update_user_status.php
4. admin_get_errands.php
5. admin_remove_errand.php
6. admin_get_reports.php
7. admin_resolve_report.php
8. admin_get_flagged_errands.php

Use this JSON response format:

```json
{
  "success": true,
  "message": "Action completed successfully",
  "data": {}
}
```

Use this JSON error format:

```json
{
  "success": false,
  "message": "Error message here"
}
```

## Android Screens to Create

Student screens:

1. Splash Screen
2. Login Screen
3. Register Screen
4. Student Dashboard
5. Post Errand Screen
6. Browse Errands Screen
7. Errand Details Screen
8. Apply as Helper Screen
9. My Posted Errands Screen
10. Applicants Screen
11. Helper Profile Preview Screen
12. My Helper Errands Screen
13. Errand Status Screen
14. Chat Screen
15. Completion Confirmation Screen
16. Rating Screen
17. Report Screen
18. Profile Screen
19. History Screen

Admin screens:

1. Admin Login or shared Login redirect
2. Admin Dashboard
3. Manage Users Screen
4. User Details Screen
5. Manage Errands Screen
6. Errand Admin Details Screen
7. Reports Screen
8. Flagged Errands Screen
9. Ratings and Feedback Screen

## Recommended Demo Flow

1. Login as requester.
2. Post an errand for buying a bluebook from the bookstore.
3. Login as helper.
4. Browse available errands.
5. Apply as helper.
6. Login as requester again.
7. View applicants.
8. Select helper.
9. Login as selected helper.
10. Accept assigned errand.
11. Send message.
12. Mark errand as In Progress.
13. Mark errand as Completed by Helper.
14. Login as requester.
15. Confirm completion.
16. Rate helper.
17. Login as admin.
18. View dashboard, users, errands, reports, ratings, and flagged content.

## Documentation Requirements

Create or update:

1. README.md
2. API_DOCUMENTATION.md
3. DATABASE_GUIDE.md
4. USER_GUIDE.md
5. EVALUATION_SUMMARY_TEMPLATE.md
6. PRESENTATION_FLOW.md

## Evaluation Form Content

Create a Google Form question list using a 5-point Likert scale. Include evaluation questions for:

1. Ease of use
2. Interface clarity
3. Usefulness
4. Safety and trust
5. Privacy confidence
6. Errand posting
7. Helper selection
8. Status tracking
9. Messaging
10. Overall satisfaction

Make sure the code is clean, organized, and understandable for students. Add comments where useful. Prioritize working features over unnecessary complexity.
