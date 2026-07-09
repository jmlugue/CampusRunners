# AGENTS.md

## Project Name

IT140P-MP-MalayanQuest

## App Display Name

Malayan Quest

## Project Type

Android Studio mobile application using the RESTful approach with PHP REST API and MySQL database.

## Project Context

Malayan Quest is a school-only micro-errand request app for students of Mapúa Malayan Colleges Laguna. The app allows verified MCL students to post small campus-based errands, apply as helpers, choose helpers, track errand status, message each other, confirm completion, rate helpers, and report unsafe activity.

The project should be built as a complete but manageable final project prototype for IT140P.

## Required School Project Compliance

This project must support the expected final project requirements:

1. Android Studio mobile application
2. RESTful approach
3. PHP REST API backend
4. MySQL database
5. Exportable SQL database file
6. Zipped project solution
7. User Experience and Usability Evaluation results
8. Google Form link and evaluation summary
9. Presentation-ready project explanation and demo flow

Do not use Supabase, Firebase, or other cloud database services as the main backend. Use PHP and MySQL so the group can submit a database file.

## Visual Identity and School Color

The app must use a clean blue-based campus theme inspired by Mapúa Malayan Colleges Laguna.

Recommended colors:

- Primary Blue: #0B4EA2
- Secondary Blue: #1976D2
- Light Blue Background: #F4F8FC
- White: #FFFFFF
- Dark Navy Text: #102A43
- Secondary Gray Text: #6B7280
- Success Green: #2E7D32
- Warning Amber: #F9A825
- Danger Red: #D32F2F

Design style:

1. Use blue for headers, primary buttons, active tabs, and important highlights.
2. Use white or light blue-gray backgrounds for readability.
3. Use card layouts for errands.
4. Use colored status badges.
5. Keep the interface clean, simple, and student-friendly.
6. Do not make the whole app dark blue.
7. Prioritize readability and usability over decorative design.

## Target Users

The system is intended for Mapúa Malayan Colleges Laguna students only.

User roles:

1. Student Requester
2. Student Helper
3. Admin

A student can act as both requester and helper after verification.

## Main Problem

Students sometimes need small school-related errands done while they are busy with classes, exams, group work, deadlines, or school activities. These requests are usually arranged through chat, which can be unorganized and unsafe because there is no structured record of the request, helper, status, agreement, or completion.

Malayan Quest solves this by providing a verified, campus-limited, traceable errand request platform.

## Main Project Goal

Build a working REST-based prototype that demonstrates:

1. Student registration and login
2. Student verification using school email and student number
3. Errand posting
4. Errand moderation
5. Available errand browsing
6. Multiple helper applications
7. Requester helper selection
8. Status updates
9. In-app messaging
10. Completion confirmation
11. Rating and feedback
12. Reporting
13. Admin monitoring and restriction controls

## School-Specific Scope

The app is for the whole Mapúa Malayan Colleges Laguna campus.

Allowed errand locations may include:

1. Canteen
2. Library
3. Bookstore
4. Printing services
5. Classrooms
6. School buildings
7. Nearby food restaurants within close proximity to MCL
8. Nearby businesses within close proximity to MCL

The app must clearly state that errands are intended only for campus or near-campus use.

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

## Banned Errands

The system must prevent, reject, or flag errands involving:

1. Illegal items or illegal activities
2. Alcohol
3. Cigarettes
4. Vape products
5. Prohibited substances
6. Medicine, supplements, or medical-related purchases
7. Dangerous tools, weapons, or harmful objects
8. Confidential school documents
9. Exams, quizzes, answer sheets, grades, IDs, or private records
10. Tasks that must personally be done by the student
11. Activities that violate school rules
12. Harassment, threats, pranks, or unsafe behavior
13. Requests involving restricted areas
14. Tasks outside reasonable campus or near-campus scope
15. Any errand that risks student safety, privacy, or security

## Verification Rules

Students register using:

1. Full name
2. School email
3. Student number
4. Password

Prototype verification rules:

1. Require school email format.
2. Require student number.
3. Prevent duplicate school email.
4. Prevent duplicate student number.
5. Mark users as verified for prototype testing after successful registration.
6. Admin can restrict or deactivate suspicious users.

Suggested account statuses:

1. active
2. deactivated

Suggested verification statuses:

1. pending
2. verified
3. rejected
4. restricted

## Privacy and Data Protection Rules

1. Do not publicly display student numbers.
2. Do not publicly display school email addresses.
3. Public helper cards should show only name, average rating, completed errand count, and offer note.
4. Store passwords using password_hash in PHP.
5. Verify passwords using password_verify in PHP.
6. Do not expose database credentials in Android.
7. All database access must go through PHP REST API endpoints.
8. Messages must only be visible to the requester and selected helper.
9. Admin may view report-related records for moderation.
10. Use prepared statements in PHP.

## Errand Status Flow

Use this main status flow:

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

Requester may cancel an errand if:

1. The errand is still Open.
2. The errand has applicants but no helper has been assigned.
3. The assigned helper has not marked it In Progress.

Helper may withdraw if:

1. The errand is not yet In Progress.

If an errand is already In Progress:

1. Cancellation requires a reason.
2. The cancellation must be recorded in errand_status_logs.
3. Admin can view the cancellation history.
4. Repeated cancellations may affect trust and reliability.

## Helper Selection Rules

The requester must be able to choose from multiple helpers.

Flow:

1. Requester posts errand.
2. Helpers apply with an offer note and estimated completion time.
3. Requester views helper name, rating, completed errand count, and offer note.
4. Requester selects one helper.
5. Selected helper receives assigned status.
6. Selected helper accepts and starts the errand.

## In-App Messaging Rules

1. Messaging is only available after a helper has been selected.
2. Messages are connected to a specific errand.
3. Only the requester and selected helper can message each other.
4. Admin may review records only when needed for report handling.

## Moderation Rules

Use rule-based moderation for the prototype.

Before an errand is posted:

1. Check errand title.
2. Check errand description.
3. Check category.
4. Check pickup and drop-off locations.
5. Detect banned keywords.
6. If safe, allow posting.
7. If suspicious, flag or reject the errand.
8. Save moderation result in moderation_logs.

The code should be modular so a real AI model or API can be added later, but the prototype must work without paid AI services.

## Admin Features

Admin can:

1. Login
2. View dashboard counts
3. View all users
4. Search users
5. View user details
6. Restrict or deactivate users
7. View all errands
8. View errand details
9. Remove inappropriate errands
10. View flagged errands
11. View reports
12. Resolve reports
13. View ratings and feedback
14. View cancellation history
15. View moderation logs

Dashboard counts:

1. Total users
2. Verified users
3. Open errands
4. Active errands
5. Completed errands
6. Cancelled errands
7. Reported errands
8. Flagged errands

## Recommended Technology Stack

Android:

1. Android Studio
2. Java
3. XML layouts
4. Retrofit or Volley
5. SharedPreferences for session handling

Backend:

1. PHP
2. MySQL
3. XAMPP
4. JSON API responses
5. Prepared statements

Database:

1. MySQL database named malayanquest_db
2. Exportable SQL file

## Required Deliverables

1. Zipped Android project
2. Backend PHP REST API files
3. MySQL database SQL file
4. Google Form link
5. Evaluation summary
6. Project presentation
7. README
8. API documentation
9. Database guide
10. User guide
11. Presentation demo flow
12. Group member details placeholder

## Development Priority

Prioritize working core features over advanced extras.

Minimum working demo:

1. Login as requester
2. Post errand
3. Login as helper
4. Browse errands
5. Apply as helper
6. Login as requester
7. Select helper
8. Login as helper
9. Accept and update status
10. Send message
11. Mark completed
12. Login as requester
13. Confirm completion
14. Rate helper
15. Login as admin
16. View dashboard and records
