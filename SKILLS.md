# SKILLS.md

## Skill 1: Android Authentication

Build registration and login screens for MCL students.

Registration fields:

1. Full name
2. School email
3. Student number
4. Password
5. Confirm password

Validation:

1. Full name is required.
2. School email is required.
3. Student number is required.
4. Password is required.
5. Confirm password must match password.
6. School email must follow the allowed school email format defined in the backend.
7. Duplicate school email must not be allowed.
8. Duplicate student number must not be allowed.

Expected output:

1. Successful registration message
2. Successful login message
3. Saved user session
4. Redirect to Student Dashboard or Admin Dashboard based on role

## Skill 2: Blue Campus Theme UI

Create a clean Android UI using MCL-inspired blue colors.

Required color resources:

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

1. Use blue for app bars, main buttons, selected navigation items, and primary actions.
2. Use white or light blue-gray for backgrounds.
3. Use cards for errand listings.
4. Use status badges.
5. Keep screens clean and readable.
6. Avoid clutter.
7. Use confirmation dialogs for important actions.

## Skill 3: Student Dashboard

Build a student dashboard with the main actions.

Required actions:

1. Post Errand
2. Browse Errands
3. My Posted Errands
4. My Helper Errands
5. Messages
6. History
7. Profile

Dashboard cards:

1. Open errands posted by the user
2. Active helper errands
3. Completed errands
4. Average rating

## Skill 4: Errand Creation

Build a form where a requester can post an errand.

Required fields:

1. Errand title
2. Errand description
3. Category
4. Pickup location
5. Drop-off location
6. Expected completion date and time
7. Optional reward amount
8. Optional reward note
9. Additional notes

Allowed categories:

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

Validation:

1. Title is required.
2. Description is required.
3. Category is required.
4. Pickup location is required.
5. Drop-off location is required.
6. Completion date and time is required.
7. Reward is optional.
8. The errand must pass moderation before it becomes visible.

## Skill 5: Errand Moderation

Build automatic rule-based moderation.

Moderation checks:

1. Title
2. Description
3. Category
4. Pickup location
5. Drop-off location

Banned content examples:

1. alcohol
2. cigarette
3. vape
4. drugs
5. medicine
6. weapon
7. exam answer
8. quiz answer
9. answer sheet
10. ID borrowing
11. confidential document
12. grade record
13. restricted area
14. illegal item
15. dangerous item
16. prank
17. harassment

Moderation results:

1. allowed
2. flagged
3. rejected

Flagged errands should be visible to admin.

## Skill 6: Browse Available Errands

Build a screen for helpers to browse open errands.

Display fields:

1. Errand title
2. Category
3. Pickup location
4. Drop-off location
5. Deadline
6. Optional reward amount
7. Optional reward note
8. Requester name
9. Requester rating
10. Current status

Filters:

1. Category
2. Location
3. Deadline
4. Reward amount
5. Search keyword

Rule:

Only show errands that are Open or Has Applicants and not posted by the logged-in user.

## Skill 7: Apply as Helper

Build a feature where students can apply to help.

Application fields:

1. Errand ID
2. Helper user ID
3. Offer note
4. Estimated completion time
5. Application status

Application status values:

1. pending
2. selected
3. not_selected
4. withdrawn

Rules:

1. A helper cannot apply to their own errand.
2. A helper cannot apply twice to the same errand.
3. A requester can view all applicants.
4. A requester chooses one helper.
5. The selected helper must accept the assigned errand.

## Skill 8: Helper Selection

Build a screen where the requester can choose from multiple helpers.

Display for each helper:

1. Name
2. Average rating
3. Completed errand count
4. Offer note
5. Estimated completion time

Requester actions:

1. Select helper
2. View helper profile
3. Cancel errand if allowed

After selecting helper:

1. Errand status becomes Assigned.
2. Selected application status becomes selected.
3. Other applications become not_selected.

## Skill 9: Errand Status Tracking

Build status tracking for requester and helper.

Status flow:

1. Open
2. Has Applicants
3. Assigned
4. Accepted
5. In Progress
6. Completed by Helper
7. Confirmed by Requester
8. Rated
9. Closed

Requester actions:

1. View status
2. Confirm completion
3. Rate helper
4. Cancel based on cancellation rules
5. Report issue

Helper actions:

1. Accept assigned errand
2. Mark as In Progress
3. Mark as Completed by Helper
4. Withdraw before In Progress
5. Report issue

Each status update must be saved in errand_status_logs.

## Skill 10: In-App Messaging

Build simple in-app messaging connected to each errand.

Rules:

1. Messaging is only available after helper selection.
2. Only requester and selected helper can message each other.
3. Messages are tied to a specific errand.

Minimum UI:

1. Message list
2. Message input box
3. Send button
4. Refresh button or automatic reload

## Skill 11: Completion and Rating

Build completion confirmation and rating.

Flow:

1. Helper marks errand as Completed by Helper.
2. Requester confirms completion.
3. Requester rates helper.
4. Errand becomes Closed.

Rating fields:

1. Errand ID
2. Rated user ID
3. Rated by user ID
4. Rating score from 1 to 5
5. Feedback comment
6. Created date and time

Rules:

1. Only requester can rate the helper after completion.
2. A user can rate only once per errand.
3. Average rating updates on helper profile.

## Skill 12: Reports and Safety

Build report features for unsafe behavior.

Report types:

1. Report errand
2. Report user
3. Report message if implemented

Report reasons:

1. Suspicious task
2. Illegal or prohibited item
3. Unsafe location
4. Harassment or inappropriate behavior
5. Fake or misleading request
6. Privacy concern
7. Other

Report statuses:

1. pending
2. under_review
3. resolved
4. dismissed

Admin should be able to view and resolve reports.

## Skill 13: Admin Dashboard

Build admin screens for monitoring and control.

Required admin features:

1. View all users
2. Search users
3. View user details
4. Deactivate or restrict users
5. View all errands
6. View errand details
7. Remove inappropriate errands
8. View flagged errands
9. View reports
10. Resolve reports
11. View ratings and feedback
12. View dashboard counts

Dashboard counts:

1. Total users
2. Verified users
3. Open errands
4. Active errands
5. Completed errands
6. Cancelled errands
7. Reported errands
8. Flagged errands

## Skill 14: Database Design

Create a MySQL database named:

malayanquest_db

Tables:

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

Include sample seed data:

1. One admin account
2. Five student accounts
3. Sample locations
4. Five sample errands
5. Sample applications
6. Sample ratings if needed

## Skill 15: PHP REST API

Create PHP REST API files.

Rules:

1. Use JSON input and output.
2. Use prepared statements.
3. Use password_hash and password_verify.
4. Do not expose database credentials in Android.
5. Validate required fields server-side.
6. Return clear success and error responses.

Success format:

```json
{
  "success": true,
  "message": "Action completed successfully",
  "data": {}
}
```

Error format:

```json
{
  "success": false,
  "message": "Error message here"
}
```

## Skill 16: Android API Integration

Connect Android screens to PHP REST endpoints.

Use:

1. Retrofit or Volley
2. JSON parsing
3. SharedPreferences for session handling
4. Loading indicators
5. Toast or dialog messages
6. Error handling

Rules:

1. Do not connect Android directly to MySQL.
2. Keep the base URL in one config file.
3. Do not hardcode production data.
4. Make sample accounts easy to test.

## Skill 17: User Experience and Usability Evaluation

Prepare testing for up to 10 MCL students.

Evaluation categories:

1. Ease of use
2. Interface clarity
3. Usefulness
4. Safety and trust
5. Privacy confidence
6. Errand posting process
7. Helper browsing and selection
8. Status tracking
9. Messaging
10. Overall satisfaction

Use a 5-point Likert scale:

1. Strongly Disagree
2. Disagree
3. Neutral
4. Agree
5. Strongly Agree

Evaluation summary should include:

1. Number of respondents
2. Average score per question
3. Overall mean
4. Interpretation
5. Short discussion of feedback
6. Suggested improvements

## Skill 18: Presentation Support

Prepare a presentation-ready demo.

Demo flow:

1. Login as requester
2. Post an errand for buying a bluebook from the bookstore
3. Login as helper
4. Browse available errands
5. Apply as helper
6. Login as requester
7. View applicants
8. Select helper
9. Login as selected helper
10. Accept assigned errand
11. Send message
12. Mark as In Progress
13. Mark as Completed by Helper
14. Login as requester
15. Confirm completion
16. Rate helper
17. Login as admin
18. View dashboard, users, errands, reports, and ratings
