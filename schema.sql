-- Database Schema tối ưu cho Spring Boot + Vue với MySQL 8.0+
-- Charset: utf8mb4, Collation: utf8mb4_unicode_ci

-- Bảng phòng ban (departments)
CREATE TABLE departments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    manager_id BIGINT NULL,
    parent_id BIGINT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES departments(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng người dùng (users)
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, -- BCrypt hash từ Spring Security
    full_name VARCHAR(255),
    role VARCHAR(20) NOT NULL DEFAULT 'USER', -- 'USER', 'ADMIN' - Enum value stored as string
    department_id BIGINT NULL,
    is_active BOOLEAN DEFAULT TRUE, -- User active status for admin management
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add foreign key constraint for department manager
ALTER TABLE departments ADD FOREIGN KEY (manager_id) REFERENCES users(id);

-- Bảng dự án (projects)
CREATE TABLE projects (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    department_id BIGINT NOT NULL,
    manager_id BIGINT NULL,
    start_date DATE,
    end_date DATE,
    status VARCHAR(20) DEFAULT 'PLANNING', -- 'PLANNING', 'ACTIVE', 'COMPLETED', 'CANCELLED'
    priority VARCHAR(20) DEFAULT 'MEDIUM', -- 'LOW', 'MEDIUM', 'HIGH', 'CRITICAL'
    budget DECIMAL(15,2),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(id),
    FOREIGN KEY (manager_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng danh mục file (file_categories)
CREATE TABLE file_categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    color VARCHAR(7), -- HEX color code cho UI
    icon VARCHAR(50), -- Icon class name
    parent_id BIGINT NULL,
    display_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES file_categories(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng file (files)
CREATE TABLE files (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(255) UNIQUE NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    content_type VARCHAR(100) NOT NULL, -- MIME type
    file_hash VARCHAR(64) UNIQUE NOT NULL, -- SHA-256 hash
    category_id BIGINT NULL,
    department_id BIGINT NULL,
    project_id BIGINT NULL,
    title VARCHAR(255),
    description TEXT,
    tags JSON, -- JSON type trong MySQL 8.0+
    visibility VARCHAR(20) DEFAULT 'PRIVATE', -- 'PRIVATE', 'PUBLIC', 'SHARED'
    download_count BIGINT DEFAULT 0,
    version INTEGER DEFAULT 1, -- File versioning
    is_deleted BOOLEAN DEFAULT FALSE, -- Soft delete
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    -- Google Drive specific fields
    drive_file_id VARCHAR(255), -- Google Drive file ID for primary storage
    drive_folder_id VARCHAR(255), -- Google Drive folder ID (optional)
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES file_categories(id),
    FOREIGN KEY (department_id) REFERENCES departments(id),
    FOREIGN KEY (project_id) REFERENCES projects(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng AI suggestions (ai_suggestions)
CREATE TABLE ai_suggestions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    file_id BIGINT NOT NULL,
    suggestion_type VARCHAR(20) NOT NULL, -- 'CATEGORY', 'TAGS', 'TITLE'
    suggested_category_id BIGINT NULL,
    suggested_title VARCHAR(255),
    suggested_tags JSON, -- JSON array of suggested tags
    confidence_score DECIMAL(5,4), -- 0.0000 to 1.0000
    ai_provider VARCHAR(50), -- 'OPENAI', 'CLAUDE', etc.
    ai_model VARCHAR(100), -- Model version
    status VARCHAR(20) DEFAULT 'PENDING', -- 'PENDING', 'ACCEPTED', 'REJECTED'
    processed_by BIGINT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP NULL,
    FOREIGN KEY (file_id) REFERENCES files(id) ON DELETE CASCADE,
    FOREIGN KEY (suggested_category_id) REFERENCES file_categories(id),
    FOREIGN KEY (processed_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng lịch sử truy cập file (file_access_logs)
CREATE TABLE file_access_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    file_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL, -- 'VIEW', 'DOWNLOAD', 'UPDATE', 'DELETE'
    ip_address VARCHAR(45), -- IPv4 và IPv6
    user_agent TEXT,
    request_time BIGINT, -- Response time in milliseconds
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (file_id) REFERENCES files(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng chia sẻ file (file_shares) - Bonus feature
CREATE TABLE file_shares (
    id BIGSERIAL PRIMARY KEY,
    file_id BIGINT NOT NULL,
    shared_by BIGINT NOT NULL,
    shared_with BIGINT NULL, -- NULL = public share
    share_token VARCHAR(255) UNIQUE, -- For public sharing
    permissions VARCHAR(20) DEFAULT 'READ', -- 'READ', 'WRITE'
    expires_at TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (file_id) REFERENCES files(id) ON DELETE CASCADE,
    FOREIGN KEY (shared_by) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (shared_with) REFERENCES users(id) ON DELETE CASCADE
);

-- Bảng cấu hình hệ thống (system_configs)
CREATE TABLE system_configs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_value TEXT,
    data_type VARCHAR(20) DEFAULT 'STRING', -- 'STRING', 'NUMBER', 'BOOLEAN', 'JSON'
    description TEXT,
    is_public BOOLEAN DEFAULT FALSE, -- Can be read by frontend
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng thông báo (notifications) - Cho tương lai
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    type VARCHAR(50) DEFAULT 'INFO', -- 'INFO', 'WARNING', 'ERROR', 'SUCCESS'
    is_read BOOLEAN DEFAULT FALSE,
    related_entity_type VARCHAR(50), -- 'FILE', 'USER', etc.
    related_entity_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indexes để tối ưu performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_is_active ON users(is_active);
CREATE INDEX idx_users_department_id ON users(department_id);
CREATE INDEX idx_departments_parent_id ON departments(parent_id);
CREATE INDEX idx_departments_manager_id ON departments(manager_id);
CREATE INDEX idx_projects_department_id ON projects(department_id);
CREATE INDEX idx_projects_manager_id ON projects(manager_id);
CREATE INDEX idx_projects_status ON projects(status);
CREATE INDEX idx_files_user_id ON files(user_id);
CREATE INDEX idx_files_category_id ON files(category_id);
CREATE INDEX idx_files_department_id ON files(department_id);
CREATE INDEX idx_files_project_id ON files(project_id);
CREATE INDEX idx_files_hash ON files(file_hash);
CREATE INDEX idx_files_uploaded_at ON files(uploaded_at);
CREATE INDEX idx_files_visibility ON files(visibility);
CREATE INDEX idx_files_deleted ON files(is_deleted);
CREATE INDEX idx_ai_suggestions_file_id ON ai_suggestions(file_id);
CREATE INDEX idx_ai_suggestions_status ON ai_suggestions(status);
CREATE INDEX idx_file_access_logs_file_id ON file_access_logs(file_id);
CREATE INDEX idx_file_access_logs_user_id ON file_access_logs(user_id);
CREATE INDEX idx_file_access_logs_created_at ON file_access_logs(created_at);
CREATE INDEX idx_file_shares_file_id ON file_shares(file_id);
CREATE INDEX idx_file_shares_token ON file_shares(share_token);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_read ON notifications(is_read);

-- JSON indexes cho MySQL 8.0+ (cho tags và suggestions)
-- MySQL sử dụng functional indexes cho JSON
ALTER TABLE files ADD INDEX idx_files_tags_functional ((CAST(tags AS CHAR(255) ARRAY)));
ALTER TABLE ai_suggestions ADD INDEX idx_ai_suggestions_tags_functional ((CAST(suggested_tags AS CHAR(255) ARRAY)));

-- Insert dữ liệu mặc định
-- Note: roles table removed - using enum directly in users table

-- Insert demo departments
INSERT INTO departments (name, description, is_active, created_at, updated_at) VALUES 
('IT Department', 'Information Technology and Software Development', true, NOW(), NOW()),
('Marketing Department', 'Marketing and Customer Relations', true, NOW(), NOW()),
('HR Department', 'Human Resources and Administration', true, NOW(), NOW()),
('Finance Department', 'Financial Management and Accounting', true, NOW(), NOW()),
('Operations Department', 'Business Operations and Process Management', true, NOW(), NOW());

INSERT INTO file_categories (name, description, color, icon, display_order) VALUES 
('Documents', 'Tài liệu văn bản (PDF, DOC, TXT)', '#2196F3', 'document', 1),
('Images', 'Hình ảnh và đồ họa', '#4CAF50', 'image', 2),
('Videos', 'Video và multimedia', '#FF9800', 'video', 3),
('Audio', 'File âm thanh và nhạc', '#9C27B0', 'audio', 4),
('Archives', 'File nén và lưu trữ', '#607D8B', 'archive', 5),
('Code', 'Mã nguồn và scripts', '#FF5722', 'code', 6),
('Others', 'Loại file khác', '#9E9E9E', 'file', 7);

INSERT INTO system_configs (config_key, config_value, data_type, description, is_public) VALUES 
('file.max-size', '104857600', 'NUMBER', 'Kích thước file tối đa (100MB)', true),
('file.allowed-types', '["pdf","doc","docx","txt","jpg","jpeg","png","gif","mp4","avi","mp3","wav","zip","rar","json","xml","csv"]', 'JSON', 'Các loại file được phép upload', true),
('ai.enabled', 'true', 'BOOLEAN', 'Bật/tắt tính năng AI gợi ý', false),
('ai.provider', 'openai', 'STRING', 'Nhà cung cấp AI service', false),
('ai.confidence-threshold', '0.7', 'NUMBER', 'Ngưỡng độ tin cậy tối thiểu', false),
('storage.path', '/app/uploads', 'STRING', 'Đường dẫn lưu trữ file', false),
('app.name', 'File Management System', 'STRING', 'Tên ứng dụng', true),
('app.version', '1.0.0', 'STRING', 'Phiên bản ứng dụng', true);

-- Insert sample admin user for testing (password: admin123)
INSERT INTO users (username, email, password, full_name, role, department_id, is_active, created_at, updated_at) VALUES 
('admin', 'admin@filehub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM9lE5fWmBnWBoa.qR8y', 'System Administrator', 'ADMIN', 1, true, NOW(), NOW()),
('user1', 'user1@filehub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM9lE5fWmBnWBoa.qR8y', 'Test User 1', 'USER', 1, true, NOW(), NOW()),
('user2', 'user2@filehub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM9lE5fWmBnWBoa.qR8y', 'Test User 2', 'USER', 2, false, NOW(), NOW());

-- Update department managers
UPDATE departments SET manager_id = 1 WHERE id = 1; -- Admin manages IT
UPDATE departments SET manager_id = 1 WHERE id = 2; -- Admin manages Marketing (temp)

-- Insert demo projects
INSERT INTO projects (name, description, department_id, manager_id, start_date, end_date, status, priority, budget, is_active, created_at, updated_at) VALUES 
('File Management System', 'Development of a comprehensive file management system with Vue.js and Spring Boot', 1, 1, '2024-01-01', '2024-12-31', 'ACTIVE', 'HIGH', 50000.00, true, NOW(), NOW()),
('Website Redesign', 'Complete redesign of company website with modern UI/UX', 2, 1, '2024-02-01', '2024-06-30', 'ACTIVE', 'MEDIUM', 25000.00, true, NOW(), NOW()),
('Employee Onboarding System', 'Digital transformation of employee onboarding process', 3, 1, '2024-03-01', '2024-09-30', 'PLANNING', 'MEDIUM', 15000.00, true, NOW(), NOW()),
('Budget Management Tool', 'Internal tool for budget tracking and financial reporting', 4, 1, '2024-04-01', '2024-10-31', 'PLANNING', 'HIGH', 30000.00, true, NOW(), NOW()),
('Process Optimization', 'Analysis and optimization of current business processes', 5, 1, '2024-05-01', '2024-11-30', 'PLANNING', 'LOW', 10000.00, true, NOW(), NOW());