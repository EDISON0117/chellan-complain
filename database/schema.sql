-- =====================================================================
-- StreetConnect Database Schema
-- Application: StreetConnect - Community Complaint & Awareness Platform
-- Database: MySQL 8.0+
-- =====================================================================

CREATE DATABASE IF NOT EXISTS streetconnect_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE streetconnect_db;

-- 1. Streets Table
CREATE TABLE IF NOT EXISTS streets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    area VARCHAR(150) NOT NULL,
    city VARCHAR(100) NOT NULL,
    pincode VARCHAR(10) NOT NULL,
    state VARCHAR(100) NOT NULL DEFAULT 'Tamil Nadu',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_street_area_city UNIQUE (name, area, city)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Categories Table
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    icon VARCHAR(50) DEFAULT 'bi-exclamation-triangle',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    mobile_number VARCHAR(15) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'ROLE_RESIDENT', -- ROLE_RESIDENT, ROLE_MODERATOR, ROLE_ADMIN
    street_id BIGINT NULL,
    profile_image VARCHAR(255) NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_street FOREIGN KEY (street_id) REFERENCES streets(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Complaints Table
CREATE TABLE IF NOT EXISTS complaints (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    category_id BIGINT NOT NULL,
    street_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    landmark VARCHAR(200),
    image_url VARCHAR(255),
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM', -- LOW, MEDIUM, HIGH, CRITICAL
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',  -- PENDING, UNDER_REVIEW, IN_PROGRESS, RESOLVED, REJECTED
    official_remark TEXT,
    resolution_notes TEXT,
    contact_number VARCHAR(20),
    resolved_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_complaints_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_complaints_street FOREIGN KEY (street_id) REFERENCES streets(id),
    CONSTRAINT fk_complaints_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Complaint Status History Table (Audit Trail)
CREATE TABLE IF NOT EXISTS complaint_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    complaint_id BIGINT NOT NULL,
    previous_status VARCHAR(20),
    new_status VARCHAR(20) NOT NULL,
    changed_by_user_id BIGINT NOT NULL,
    remark TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_history_complaint FOREIGN KEY (complaint_id) REFERENCES complaints(id) ON DELETE CASCADE,
    CONSTRAINT fk_history_user FOREIGN KEY (changed_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Comments Table
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    complaint_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_comments_complaint FOREIGN KEY (complaint_id) REFERENCES complaints(id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Supports / Upvotes Table
CREATE TABLE IF NOT EXISTS supports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    complaint_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_complaint_user_support UNIQUE (complaint_id, user_id),
    CONSTRAINT fk_supports_complaint FOREIGN KEY (complaint_id) REFERENCES complaints(id) ON DELETE CASCADE,
    CONSTRAINT fk_supports_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    link VARCHAR(255),
    type VARCHAR(50) DEFAULT 'GENERAL', -- STATUS_UPDATE, COMMENT, SUPPORT, ANNOUNCEMENT, GENERAL
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. Reports Table (Inappropriate content flagging)
CREATE TABLE IF NOT EXISTS reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    complaint_id BIGINT NOT NULL,
    reported_by_user_id BIGINT NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(30) DEFAULT 'PENDING', -- PENDING, REVIEWED, DISMISSED, ACTION_TAKEN
    action_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reports_complaint FOREIGN KEY (complaint_id) REFERENCES complaints(id) ON DELETE CASCADE,
    CONSTRAINT fk_reports_user FOREIGN KEY (reported_by_user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. Community Announcements Table
CREATE TABLE IF NOT EXISTS announcements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    street_id BIGINT NULL, -- NULL means global announcement across all streets
    created_by_user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    priority VARCHAR(20) DEFAULT 'NORMAL', -- NORMAL, IMPORTANT, URGENT
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_announcements_street FOREIGN KEY (street_id) REFERENCES streets(id) ON DELETE CASCADE,
    CONSTRAINT fk_announcements_user FOREIGN KEY (created_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indexes for performance
CREATE INDEX idx_complaints_street ON complaints(street_id);
CREATE INDEX idx_complaints_status ON complaints(status);
CREATE INDEX idx_complaints_category ON complaints(category_id);
CREATE INDEX idx_complaints_priority ON complaints(priority);
CREATE INDEX idx_complaints_created ON complaints(created_at);
CREATE INDEX idx_comments_complaint ON comments(complaint_id);
CREATE INDEX idx_supports_complaint ON supports(complaint_id);
CREATE INDEX idx_notifications_user ON notifications(user_id, is_read);
