-- demo_seed_records.sql
-- Idempotent demo data for Malayan Quest presentation screens.
-- Safe to run repeatedly after importing malayanquest_db.sql.

USE malayanquest_db;

SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO users (user_id, full_name, school_email, student_number, password_hash, role, verification_status, account_status, average_rating, completed_errands)
VALUES
(1, 'Malayan Quest Admin', 'admin@mcl.edu.ph', NULL, '$2y$10$mlcMIfNWx9ZCZ.x0TmC6zORj29Pvu/s4d/Jqt4elYu5.On3T92qQ6', 'admin', 'verified', 'active', 0.00, 0),
(2, 'Juan Dela Cruz', '2026juandcruz@live.mcl.edu.ph', '2026000001', '$2y$10$mlcMIfNWx9ZCZ.x0TmC6zORj29Pvu/s4d/Jqt4elYu5.On3T92qQ6', 'student', 'verified', 'active', 4.90, 8),
(3, 'Maria Santos', '2026mariasantos@live.mcl.edu.ph', '2026000002', '$2y$10$mlcMIfNWx9ZCZ.x0TmC6zORj29Pvu/s4d/Jqt4elYu5.On3T92qQ6', 'student', 'verified', 'active', 4.75, 6),
(4, 'Carlo Reyes', '2026carloreyes@live.mcl.edu.ph', '2026000003', '$2y$10$mlcMIfNWx9ZCZ.x0TmC6zORj29Pvu/s4d/Jqt4elYu5.On3T92qQ6', 'student', 'verified', 'active', 4.60, 5),
(5, 'Ana Lopez', '2026analopez@live.mcl.edu.ph', '2026000004', '$2y$10$mlcMIfNWx9ZCZ.x0TmC6zORj29Pvu/s4d/Jqt4elYu5.On3T92qQ6', 'student', 'verified', 'active', 4.80, 7),
(6, 'Miguel Garcia', '2026miguelgarcia@live.mcl.edu.ph', '2026000005', '$2y$10$mlcMIfNWx9ZCZ.x0TmC6zORj29Pvu/s4d/Jqt4elYu5.On3T92qQ6', 'student', 'verified', 'active', 4.20, 3),
(7, 'Lara Mendoza', '2026laramendoza@live.mcl.edu.ph', '2026000006', '$2y$10$mlcMIfNWx9ZCZ.x0TmC6zORj29Pvu/s4d/Jqt4elYu5.On3T92qQ6', 'student', 'verified', 'active', 4.95, 11),
(8, 'Rafael Cruz', '2026rafaelcruz@live.mcl.edu.ph', '2026000007', '$2y$10$mlcMIfNWx9ZCZ.x0TmC6zORj29Pvu/s4d/Jqt4elYu5.On3T92qQ6', 'student', 'restricted', 'active', 2.10, 1),
(9, 'Nina Torres', '2026ninatorres@live.mcl.edu.ph', '2026000008', '$2y$10$mlcMIfNWx9ZCZ.x0TmC6zORj29Pvu/s4d/Jqt4elYu5.On3T92qQ6', 'student', 'verified', 'deactivated', 3.00, 2),
(10, 'Paolo Rivera', '2026paolorivera@live.mcl.edu.ph', '2026000009', '$2y$10$mlcMIfNWx9ZCZ.x0TmC6zORj29Pvu/s4d/Jqt4elYu5.On3T92qQ6', 'student', 'pending', 'active', 0.00, 0)
ON DUPLICATE KEY UPDATE
full_name = VALUES(full_name),
role = VALUES(role),
verification_status = VALUES(verification_status),
account_status = VALUES(account_status),
average_rating = VALUES(average_rating),
completed_errands = VALUES(completed_errands);

-- Errands (Categories UPDATED)
INSERT INTO errands (errand_id, requester_id, selected_helper_id, title, description, category, pickup_location, dropoff_location, deadline, reward_amount, reward_note, status, moderation_status, created_at)
VALUES
(1, 2, NULL, 'Buy bluebook from bookstore', 'Please buy one bluebook from the bookstore before my quiz.', 'Bluebook', 'Bookstore', 'Room A301', '2026-07-15 13:00:00', 20.00, 'Cash after delivery', 'Has Applicants', 'allowed', '2026-07-12 08:30:00'),
(2, 3, NULL, 'Pick up printed handouts', 'Please pick up my printed handouts from printing services.', 'Printing', 'Printing Services', 'Library entrance', '2026-07-15 11:30:00', 15.00, 'Will pay upon handoff', 'Has Applicants', 'allowed', '2026-07-12 09:00:00'),
(3, 4, NULL, 'Buy lunch from canteen', 'Please buy one rice meal and bottled water from the canteen.', 'Food', 'Canteen', 'Classroom Building', '2026-07-15 12:00:00', 25.00, 'Payment after delivery', 'Open', 'allowed', '2026-07-12 09:15:00'),
(4, 5, 2, 'Deliver project materials', 'Bring light project materials from Room B202 to Room C105.', 'Delivery', 'Room B202', 'Room C105', '2026-07-16 10:00:00', 10.00, 'Thank you reward', 'Assigned', 'allowed', '2026-07-12 10:00:00'),
(5, 2, 3, 'Bring calculator from library', 'Please bring my calculator from the library desk to Room A210.', 'Delivery', 'Library', 'Room A210', '2026-07-15 14:00:00', 30.00, 'Handle carefully', 'In Progress', 'allowed', '2026-07-12 10:30:00'),
(6, 2, 4, 'Pick up printed thesis draft', 'Pick up my printed thesis draft from printing services.', 'Printing', 'Printing Services', 'Room A305', '2026-07-15 15:00:00', 60.00, 'Includes printing fee reimbursement', 'Completed by Helper', 'allowed', '2026-07-12 11:00:00'),
(7, 2, 5, 'Buy graphing paper', 'Please buy graphing paper from the bookstore.', 'Supplies', 'Bookstore', 'Library', '2026-07-14 16:00:00', 20.00, 'Will pay in cash', 'Confirmed by Requester', 'allowed', '2026-07-11 13:00:00'),
(8, 3, 2, 'Classroom-to-classroom delivery', 'Deliver a sealed envelope from Room A201 to Room B104.', 'Delivery', 'Room A201', 'Room B104', '2026-07-10 10:00:00', 25.00, 'Completed successfully', 'Closed', 'allowed', '2026-07-10 08:00:00'),
(9, 6, 2, 'Check bookstore graphing paper', 'Check if the bookstore has graphing paper available.', 'Supplies', 'Bookstore', 'Library', '2026-07-11 14:00:00', NULL, 'No purchase needed', 'Cancelled by Requester', 'allowed', '2026-07-10 09:30:00'),
(10, 7, NULL, 'Errand to restricted office', 'Please get a document from a restricted office area.', 'Delivery', 'Restricted Office', 'Lobby', '2026-07-16 10:00:00', 40.00, 'Needs review', 'Open', 'flagged', '2026-07-12 12:00:00'),
(11, 8, NULL, 'Pick up confidential exam paper', 'Please pick up a confidential exam paper for me.', 'Delivery', 'Faculty Room', 'Room C204', '2026-07-16 09:00:00', 100.00, 'Unsafe request', 'Open', 'flagged', '2026-07-12 12:15:00'),
(12, 9, NULL, 'Buy medicine nearby', 'Please buy medicine from a nearby pharmacy.', 'Others', 'Nearby Pharmacy', 'Campus Gate', '2026-07-16 11:00:00', 30.00, 'Medical purchase is not allowed', 'Open', 'flagged', '2026-07-12 12:30:00'),
(13, 4, 2, 'Deliver library book return', 'Return a borrowed library book before closing.', 'Delivery', 'Room C105', 'Library', '2026-07-15 17:00:00', 20.00, 'Book is ready for pickup', 'Accepted', 'allowed', '2026-07-12 13:00:00')
ON DUPLICATE KEY UPDATE
requester_id = VALUES(requester_id),
selected_helper_id = VALUES(selected_helper_id),
title = VALUES(title),
description = VALUES(description),
category = VALUES(category),
pickup_location = VALUES(pickup_location),
dropoff_location = VALUES(dropoff_location),
deadline = VALUES(deadline),
reward_amount = VALUES(reward_amount),
reward_note = VALUES(reward_note),
status = VALUES(status),
moderation_status = VALUES(moderation_status);

INSERT INTO errand_applications (application_id, errand_id, helper_id, offer_note, estimated_completion_time, status, created_at)
VALUES
(1, 1, 3, 'I can pass by the bookstore before my next class.', '30 minutes', 'pending', '2026-07-12 08:45:00'),
(2, 1, 4, 'I am already near the bookstore.', '20 minutes', 'pending', '2026-07-12 08:50:00'),
(3, 1, 5, 'I can buy it after my library visit.', '35 minutes', 'pending', '2026-07-12 08:55:00'),
(4, 2, 2, 'I can pick these up after my 10:30 class.', '25 minutes', 'pending', '2026-07-12 09:10:00'),
(5, 4, 2, 'I can deliver these materials before lunch.', '30 minutes', 'selected', '2026-07-12 10:10:00'),
(6, 5, 3, 'I will bring it to A210 carefully.', '20 minutes', 'selected', '2026-07-12 10:40:00'),
(7, 6, 4, 'I am at printing services now.', '15 minutes', 'selected', '2026-07-12 11:10:00'),
(8, 7, 5, 'I can buy it after my class.', '25 minutes', 'selected', '2026-07-11 13:10:00'),
(9, 8, 2, 'I can deliver the envelope on my way to B building.', '10 minutes', 'selected', '2026-07-10 08:10:00'),
(10, 9, 2, 'I can check the bookstore after class.', '20 minutes', 'withdrawn', '2026-07-10 09:45:00'),
(11, 13, 2, 'I can return the book before library closing.', '20 minutes', 'selected', '2026-07-12 13:10:00')
ON DUPLICATE KEY UPDATE
offer_note = VALUES(offer_note),
estimated_completion_time = VALUES(estimated_completion_time),
status = VALUES(status);

-- Remove the obsolete demo log that changed the errand workflow status to Reported.
-- Reporting should remain in the reports table and must not replace errands.status.
DELETE FROM errand_status_logs
WHERE log_id = 9
  AND errand_id = 10
  AND old_status = 'Open'
  AND new_status = 'Reported';

INSERT INTO errand_status_logs (log_id, errand_id, changed_by, old_status, new_status, reason, created_at)
VALUES
(1, 1, 3, 'Open', 'Has Applicants', 'Maria applied as helper.', '2026-07-12 08:45:00'),
(2, 2, 2, 'Open', 'Has Applicants', 'Juan applied as helper.', '2026-07-12 09:10:00'),
(3, 4, 5, 'Has Applicants', 'Assigned', 'Requester selected Juan as helper.', '2026-07-12 10:15:00'),
(4, 5, 3, 'Accepted', 'In Progress', 'Helper started the errand.', '2026-07-12 10:55:00'),
(5, 6, 4, 'In Progress', 'Completed by Helper', 'Helper marked pickup completed.', '2026-07-12 11:45:00'),
(6, 7, 2, 'Completed by Helper', 'Confirmed by Requester', 'Requester confirmed delivery.', '2026-07-11 14:10:00'),
(7, 8, 3, 'Rated', 'Closed', 'Requester submitted rating.', '2026-07-10 10:20:00'),
(8, 9, 6, 'Open', 'Cancelled by Requester', 'Item was no longer needed.', '2026-07-10 10:00:00'),
(10, 13, 2, 'Assigned', 'Accepted', 'Helper accepted the selected errand.', '2026-07-12 13:20:00')
ON DUPLICATE KEY UPDATE
old_status = VALUES(old_status),
new_status = VALUES(new_status),
reason = VALUES(reason);

INSERT INTO messages (message_id, errand_id, sender_id, receiver_id, message_text, is_read, created_at)
VALUES
(1, 4, 5, 2, 'Hi Juan, the project materials are ready at Room B202.', 1, '2026-07-12 10:20:00'),
(2, 4, 2, 5, 'Got it. I will deliver them before lunch.', 1, '2026-07-12 10:22:00'),
(3, 5, 2, 3, 'Maria, please check the library front desk for the calculator.', 1, '2026-07-12 10:45:00'),
(4, 5, 3, 2, 'I have it. I am walking to Room A210 now.', 0, '2026-07-12 10:58:00'),
(5, 8, 3, 2, 'Thanks for delivering the envelope safely.', 1, '2026-07-10 10:15:00'),
(6, 8, 2, 3, 'You are welcome. It was delivered to Room B104.', 1, '2026-07-10 10:16:00'),
(7, 13, 4, 2, 'Please return the book before 5 PM.', 0, '2026-07-12 13:25:00'),
(8, 13, 2, 4, 'Accepted. I will go to the library after class.', 0, '2026-07-12 13:28:00')
ON DUPLICATE KEY UPDATE
message_text = VALUES(message_text),
is_read = VALUES(is_read);

INSERT INTO ratings (rating_id, errand_id, rated_user_id, rated_by_user_id, rating_score, feedback, created_at)
VALUES
(1, 8, 2, 3, 5, 'Fast, polite, and gave clear updates.', '2026-07-10 10:25:00'),
(2, 7, 5, 2, 5, 'Ana delivered the graphing paper quickly.', '2026-07-11 14:15:00'),
(3, 6, 4, 2, 4, 'Carlo completed the printing pickup on time.', '2026-07-12 11:50:00'),
(4, 5, 3, 2, 5, 'Maria handled the calculator carefully.', '2026-07-12 11:05:00')
ON DUPLICATE KEY UPDATE
rating_score = VALUES(rating_score),
feedback = VALUES(feedback);

INSERT INTO reports (report_id, errand_id, reported_user_id, reported_by_user_id, report_type, reason, details, status, created_at, resolved_at)
VALUES
(1, 10, NULL, 2, 'errand', 'Unsafe location', 'The errand asks a student to enter a restricted office.', 'pending', '2026-07-12 12:20:00', NULL),
(2, NULL, 8, 3, 'user', 'Harassment or inappropriate behavior', 'The user sent rude messages after a helper declined.', 'under_review', '2026-07-12 12:25:00', NULL),
(3, 11, 8, 4, 'errand', 'Illegal or prohibited item', 'The request involves confidential exam material.', 'resolved', '2026-07-12 12:35:00', '2026-07-12 13:00:00'),
(4, 12, 9, 5, 'errand', 'Medicine or medical-related purchase', 'Medicine purchases are banned by prototype rules.', 'pending', '2026-07-12 12:40:00', NULL)
ON DUPLICATE KEY UPDATE
reason = VALUES(reason),
details = VALUES(details),
status = VALUES(status),
resolved_at = VALUES(resolved_at);

INSERT INTO moderation_logs (moderation_id, errand_id, checked_title, checked_description, result, matched_terms, created_at)
VALUES
(1, 1, 'Buy bluebook from bookstore', 'Please buy one bluebook from the bookstore before my quiz.', 'allowed', NULL, '2026-07-12 08:30:01'),
(2, 10, 'Errand to restricted office', 'Please get a document from a restricted office area.', 'flagged', 'restricted office, document', '2026-07-12 12:00:01'),
(3, 11, 'Pick up confidential exam paper', 'Please pick up a confidential exam paper for me.', 'rejected', 'confidential, exam paper', '2026-07-12 12:15:01'),
(4, 12, 'Buy medicine nearby', 'Please buy medicine from a nearby pharmacy.', 'flagged', 'medicine, pharmacy', '2026-07-12 12:30:01'),
(5, 13, 'Deliver library book return', 'Return a borrowed library book before closing.', 'allowed', NULL, '2026-07-12 13:00:01')
ON DUPLICATE KEY UPDATE
result = VALUES(result),
matched_terms = VALUES(matched_terms);

INSERT INTO admin_actions (action_id, admin_id, target_user_id, target_errand_id, action_type, action_details, created_at)
VALUES
(1, 1, 8, NULL, 'restrict_user', 'Restricted for unsafe report history in demo data.', '2026-07-12 13:05:00'),
(2, 1, NULL, 11, 'review_flagged_errand', 'Reviewed confidential exam paper request.', '2026-07-12 13:10:00'),
(3, 1, NULL, 3, 'resolve_report', 'Resolved report after confirming policy violation.', '2026-07-12 13:15:00')
ON DUPLICATE KEY UPDATE
action_details = VALUES(action_details);

SET FOREIGN_KEY_CHECKS = 1;

ALTER TABLE users AUTO_INCREMENT = 100;
ALTER TABLE errands AUTO_INCREMENT = 100;
ALTER TABLE errand_applications AUTO_INCREMENT = 100;
ALTER TABLE errand_status_logs AUTO_INCREMENT = 100;
ALTER TABLE messages AUTO_INCREMENT = 100;
ALTER TABLE ratings AUTO_INCREMENT = 100;
ALTER TABLE reports AUTO_INCREMENT = 100;
ALTER TABLE moderation_logs AUTO_INCREMENT = 100;
ALTER TABLE admin_actions AUTO_INCREMENT = 100;