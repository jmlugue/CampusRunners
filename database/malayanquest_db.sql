-- malayanquest_db.sql
-- Database starter schema for IT140P-MP-MalayanQuest
-- App display name: Malayan Quest

CREATE DATABASE IF NOT EXISTS malayanquest_db;
USE malayanquest_db;

DROP TABLE IF EXISTS admin_actions;
DROP TABLE IF EXISTS moderation_logs;
DROP TABLE IF EXISTS reports;
DROP TABLE IF EXISTS ratings;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS errand_status_logs;
DROP TABLE IF EXISTS errand_applications;
DROP TABLE IF EXISTS errands;
DROP TABLE IF EXISTS locations;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    school_email VARCHAR(150) NOT NULL UNIQUE,
    student_number VARCHAR(50) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('student','admin') NOT NULL DEFAULT 'student',
    verification_status ENUM('pending','verified','rejected','restricted') NOT NULL DEFAULT 'pending',
    account_status ENUM('active','deactivated') NOT NULL DEFAULT 'active',
    average_rating DECIMAL(3,2) NOT NULL DEFAULT 0.00,
    completed_errands INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE locations (
    location_id INT AUTO_INCREMENT PRIMARY KEY,
    location_name VARCHAR(150) NOT NULL,
    location_type VARCHAR(100) NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1
);

CREATE TABLE errands (
    errand_id INT AUTO_INCREMENT PRIMARY KEY,
    requester_id INT NOT NULL,
    selected_helper_id INT NULL,
    title VARCHAR(180) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(100) NOT NULL,
    pickup_location VARCHAR(150) NOT NULL,
    dropoff_location VARCHAR(150) NOT NULL,
    deadline DATETIME NOT NULL,
    reward_amount DECIMAL(10,2) NULL,
    reward_note VARCHAR(180) NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'Open',
    moderation_status ENUM('allowed','flagged','rejected') NOT NULL DEFAULT 'allowed',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (requester_id) REFERENCES users(user_id),
    FOREIGN KEY (selected_helper_id) REFERENCES users(user_id)
);

CREATE TABLE errand_applications (
    application_id INT AUTO_INCREMENT PRIMARY KEY,
    errand_id INT NOT NULL,
    helper_id INT NOT NULL,
    offer_note TEXT NULL,
    estimated_completion_time VARCHAR(100) NULL,
    status ENUM('pending','selected','not_selected','withdrawn') NOT NULL DEFAULT 'pending',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_helper_application (errand_id, helper_id),
    FOREIGN KEY (errand_id) REFERENCES errands(errand_id) ON DELETE CASCADE,
    FOREIGN KEY (helper_id) REFERENCES users(user_id)
);

CREATE TABLE errand_status_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    errand_id INT NOT NULL,
    changed_by INT NOT NULL,
    old_status VARCHAR(50) NULL,
    new_status VARCHAR(50) NOT NULL,
    reason TEXT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (errand_id) REFERENCES errands(errand_id) ON DELETE CASCADE,
    FOREIGN KEY (changed_by) REFERENCES users(user_id)
);

CREATE TABLE messages (
    message_id INT AUTO_INCREMENT PRIMARY KEY,
    errand_id INT NOT NULL,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    message_text TEXT NOT NULL,
    is_read TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (errand_id) REFERENCES errands(errand_id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(user_id),
    FOREIGN KEY (receiver_id) REFERENCES users(user_id)
);

CREATE TABLE ratings (
    rating_id INT AUTO_INCREMENT PRIMARY KEY,
    errand_id INT NOT NULL,
    rated_user_id INT NOT NULL,
    rated_by_user_id INT NOT NULL,
    rating_score INT NOT NULL,
    feedback TEXT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_errand_rating (errand_id, rated_user_id, rated_by_user_id),
    FOREIGN KEY (errand_id) REFERENCES errands(errand_id) ON DELETE CASCADE,
    FOREIGN KEY (rated_user_id) REFERENCES users(user_id),
    FOREIGN KEY (rated_by_user_id) REFERENCES users(user_id),
    CHECK (rating_score BETWEEN 1 AND 5)
);

CREATE TABLE reports (
    report_id INT AUTO_INCREMENT PRIMARY KEY,
    errand_id INT NULL,
    reported_user_id INT NULL,
    reported_by_user_id INT NOT NULL,
    report_type VARCHAR(80) NOT NULL,
    reason VARCHAR(120) NOT NULL,
    details TEXT NULL,
    status ENUM('pending','under_review','resolved','dismissed') NOT NULL DEFAULT 'pending',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at DATETIME NULL,
    FOREIGN KEY (errand_id) REFERENCES errands(errand_id) ON DELETE SET NULL,
    FOREIGN KEY (reported_user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (reported_by_user_id) REFERENCES users(user_id)
);

CREATE TABLE moderation_logs (
    moderation_id INT AUTO_INCREMENT PRIMARY KEY,
    errand_id INT NULL,
    checked_title VARCHAR(180) NOT NULL,
    checked_description TEXT NOT NULL,
    result ENUM('allowed','flagged','rejected') NOT NULL,
    matched_terms TEXT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (errand_id) REFERENCES errands(errand_id) ON DELETE SET NULL
);

CREATE TABLE admin_actions (
    action_id INT AUTO_INCREMENT PRIMARY KEY,
    admin_id INT NOT NULL,
    target_user_id INT NULL,
    target_errand_id INT NULL,
    action_type VARCHAR(100) NOT NULL,
    action_details TEXT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES users(user_id),
    FOREIGN KEY (target_user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (target_errand_id) REFERENCES errands(errand_id) ON DELETE SET NULL
);

-- Sample users
-- Demo password for all seed accounts: password
INSERT INTO users (full_name, school_email, student_number, password_hash, role, verification_status, account_status)
VALUES
('Malayan Quest Admin', 'admin@mcl.edu.ph', NULL, '$2y$10$mlcMIfNWx9ZCZ.x0TmC6zORj29Pvu/s4d/Jqt4elYu5.On3T92qQ6', 'admin', 'verified', 'active'),
('Juan Dela Cruz', '2026juandcruz@live.mcl.edu.ph', '2026000001', '$2y$10$mlcMIfNWx9ZCZ.x0TmC6zORj29Pvu/s4d/Jqt4elYu5.On3T92qQ6', 'student', 'verified', 'active'),
('Maria Santos', '2026mariasantos@live.mcl.edu.ph', '2026000002', '$2y$10$mlcMIfNWx9ZCZ.x0TmC6zORj29Pvu/s4d/Jqt4elYu5.On3T92qQ6', 'student', 'verified', 'active'),
('Carlo Reyes', '2026carloreyes@live.mcl.edu.ph', '2026000003', '$2y$10$mlcMIfNWx9ZCZ.x0TmC6zORj29Pvu/s4d/Jqt4elYu5.On3T92qQ6', 'student', 'verified', 'active'),
('Ana Lopez', '2026analopez@live.mcl.edu.ph', '2026000004', '$2y$10$mlcMIfNWx9ZCZ.x0TmC6zORj29Pvu/s4d/Jqt4elYu5.On3T92qQ6', 'student', 'verified', 'active'),
('Miguel Garcia', '2026miguelgarcia@live.mcl.edu.ph', '2026000005', '$2y$10$mlcMIfNWx9ZCZ.x0TmC6zORj29Pvu/s4d/Jqt4elYu5.On3T92qQ6', 'student', 'verified', 'active');

-- Sample locations
INSERT INTO locations (location_name, location_type)
VALUES
('Canteen', 'Campus'),
('Library', 'Campus'),
('Bookstore', 'Campus'),
('Printing Services', 'Campus'),
('Classroom Building', 'Campus'),
('Nearby Food Restaurant', 'Near-MCL'),
('Nearby Business', 'Near-MCL');

-- Sample errands (Categories UPDATED)
INSERT INTO errands (requester_id, title, description, category, pickup_location, dropoff_location, deadline, reward_amount, reward_note, status, moderation_status)
VALUES
(2, 'Buy bluebook from bookstore', 'Please buy one bluebook from the bookstore before my quiz.', 'Bluebook', 'Bookstore', 'Room A301', '2026-07-15 13:00:00', 20.00, 'Cash after delivery', 'Open', 'allowed'),
(3, 'Pick up printed handouts', 'Please pick up my printed handouts from printing services.', 'Printing', 'Printing Services', 'Library entrance', '2026-07-15 11:30:00', 15.00, 'Will pay upon handoff', 'Open', 'allowed'),
(4, 'Buy lunch from canteen', 'Please buy one rice meal from the canteen.', 'Food', 'Canteen', 'Classroom Building', '2026-07-15 12:00:00', 25.00, 'Payment after delivery', 'Open', 'allowed'),
(5, 'Deliver project materials', 'Please bring light project materials from Room B202 to Room C105.', 'Delivery', 'Room B202', 'Room C105', '2026-07-16 10:00:00', 10.00, 'Thank you reward', 'Open', 'allowed'),
(6, 'Check bookstore stock', 'Please check if the bookstore has graphing paper available.', 'Supplies', 'Bookstore', 'Library', '2026-07-16 14:00:00', NULL, 'No purchase needed, just check availability', 'Open', 'allowed');

-- Sample activity
INSERT INTO errand_applications (errand_id, helper_id, offer_note, estimated_completion_time, status)
VALUES
(1, 3, 'I can pass by the bookstore before my next class.', '30 minutes', 'pending'),
(2, 4, 'I am near printing services right now.', '15 minutes', 'pending');

UPDATE errands SET status = 'Has Applicants' WHERE errand_id IN (1, 2);

INSERT INTO errand_status_logs (errand_id, changed_by, old_status, new_status, reason)
VALUES
(1, 3, 'Open', 'Has Applicants', 'Seed helper application submitted.'),
(2, 4, 'Open', 'Has Applicants', 'Seed helper application submitted.');

-- For a fully populated presentation database, import database/demo_seed_records.sql
-- after this file. The demo seed is idempotent and can be run repeatedly.
