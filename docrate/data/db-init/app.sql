SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

CREATE DATABASE IF NOT EXISTS app
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;


USE app;


-- --------------------------------------------------
-- DROP TABLE
-- --------------------------------------------------
DROP TABLE IF EXISTS doctor_requests;
DROP TABLE IF EXISTS hospital_requests;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS doctors;
DROP TABLE IF EXISTS departments;
DROP TABLE IF EXISTS hospitals;
DROP TABLE IF EXISTS users;

-- --------------------------------------------------
-- users
-- --------------------------------------------------
CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email),
    UNIQUE KEY uk_users_nickname (nickname),
    KEY idx_users_role (role),
    KEY idx_users_created_at (created_at),
    CONSTRAINT chk_users_role CHECK (role IN ('USER', 'ADMIN'))
)CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- --------------------------------------------------
-- hospitals
-- --------------------------------------------------
CREATE TABLE hospitals (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(30),
    category VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_hospitals_name_address (name, address),
    KEY idx_hospitals_name (name),
    KEY idx_hospitals_category (category),
    KEY idx_hospitals_status (status),
    KEY idx_hospitals_status_category (status, category),
    KEY idx_hospitals_created_at (created_at),
    CONSTRAINT chk_hospitals_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
)CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- --------------------------------------------------
-- departments
-- --------------------------------------------------
CREATE TABLE departments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_departments_name (name)
)CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- --------------------------------------------------
-- doctors
-- --------------------------------------------------
CREATE TABLE doctors (
    id BIGINT NOT NULL AUTO_INCREMENT,
    hospital_id BIGINT NOT NULL,
    department_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    intro TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_doctors_hospital_department_name (hospital_id, department_id, name),
    KEY idx_doctors_hospital_id (hospital_id),
    KEY idx_doctors_department_id (department_id),
    KEY idx_doctors_name (name),
    KEY idx_doctors_status (status),
    KEY idx_doctors_hospital_status (hospital_id, status),
    KEY idx_doctors_department_status (department_id, status),
    KEY idx_doctors_hospital_department_status (hospital_id, department_id, status),
    KEY idx_doctors_created_at (created_at),
    CONSTRAINT fk_doctors_hospital
        FOREIGN KEY (hospital_id) REFERENCES hospitals(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_doctors_department
        FOREIGN KEY (department_id) REFERENCES departments(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_doctors_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
)CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- --------------------------------------------------
-- reviews
-- --------------------------------------------------
CREATE TABLE reviews (
    id BIGINT NOT NULL AUTO_INCREMENT,
    doctor_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating TINYINT NOT NULL,
    bedside_manner TINYINT NOT NULL,
    explanation TINYINT NOT NULL,
    wait_time TINYINT NOT NULL,
    revisit_intention BOOLEAN NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_reviews_doctor_id (doctor_id),
    KEY idx_reviews_user_id (user_id),
    KEY idx_reviews_doctor_created_at (doctor_id, created_at),
    KEY idx_reviews_user_created_at (user_id, created_at),
    KEY idx_reviews_doctor_rating (doctor_id, rating),
    KEY idx_reviews_created_at (created_at),
    CONSTRAINT fk_reviews_doctor
        FOREIGN KEY (doctor_id) REFERENCES doctors(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_reviews_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_reviews_rating CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT chk_reviews_bedside_manner CHECK (bedside_manner BETWEEN 1 AND 5),
    CONSTRAINT chk_reviews_explanation CHECK (explanation BETWEEN 1 AND 5),
    CONSTRAINT chk_reviews_wait_time CHECK (wait_time BETWEEN 1 AND 5)
)CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- --------------------------------------------------
-- hospital_requests
-- --------------------------------------------------
CREATE TABLE hospital_requests (
    id BIGINT NOT NULL AUTO_INCREMENT,
    requester_user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(30),
    category VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    approved_hospital_id BIGINT NULL,
    rejection_reason VARCHAR(255) NULL,
    approved_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_hospital_requests_requester_user_id (requester_user_id),
    KEY idx_hospital_requests_status (status),
    KEY idx_hospital_requests_category (category),
    KEY idx_hospital_requests_approved_hospital_id (approved_hospital_id),
    KEY idx_hospital_requests_status_created_at (status, created_at),
    KEY idx_hospital_requests_requester_status (requester_user_id, status),
    KEY idx_hospital_requests_created_at (created_at),
    CONSTRAINT fk_hospital_requests_requester_user
        FOREIGN KEY (requester_user_id) REFERENCES users(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_hospital_requests_approved_hospital
        FOREIGN KEY (approved_hospital_id) REFERENCES hospitals(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT chk_hospital_requests_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
)CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- --------------------------------------------------
-- doctor_requests
-- --------------------------------------------------
CREATE TABLE doctor_requests (
    id BIGINT NOT NULL AUTO_INCREMENT,
    requester_user_id BIGINT NOT NULL,
    hospital_id BIGINT NOT NULL,
    department_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    intro TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    approved_doctor_id BIGINT NULL,
    rejection_reason VARCHAR(255) NULL,
    approved_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_doctor_requests_requester_user_id (requester_user_id),
    KEY idx_doctor_requests_hospital_id (hospital_id),
    KEY idx_doctor_requests_department_id (department_id),
    KEY idx_doctor_requests_status (status),
    KEY idx_doctor_requests_approved_doctor_id (approved_doctor_id),
    KEY idx_doctor_requests_status_created_at (status, created_at),
    KEY idx_doctor_requests_hospital_status (hospital_id, status),
    KEY idx_doctor_requests_department_status (department_id, status),
    KEY idx_doctor_requests_requester_status (requester_user_id, status),
    KEY idx_doctor_requests_created_at (created_at),
    CONSTRAINT fk_doctor_requests_requester_user
        FOREIGN KEY (requester_user_id) REFERENCES users(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_doctor_requests_hospital
        FOREIGN KEY (hospital_id) REFERENCES hospitals(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_doctor_requests_department
        FOREIGN KEY (department_id) REFERENCES departments(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_doctor_requests_approved_doctor
        FOREIGN KEY (approved_doctor_id) REFERENCES doctors(id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT chk_doctor_requests_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
)CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

SHOW TABLES;
