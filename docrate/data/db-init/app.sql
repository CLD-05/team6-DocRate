USE app;

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
);

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
);
