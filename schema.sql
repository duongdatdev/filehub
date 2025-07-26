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
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add foreign key constraint for department manager
ALTER TABLE departments ADD FOREIGN KEY (manager_id) REFERENCES users(id);

-- Bảng quan hệ nhiều-nhiều giữa users và departments (user_departments)
CREATE TABLE user_departments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    department_id BIGINT NOT NULL,
    role VARCHAR(50) DEFAULT 'MEMBER', -- 'MEMBER', 'MANAGER', 'DEPUTY_MANAGER'
    is_active BOOLEAN DEFAULT TRUE,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_by BIGINT NULL, -- Who assigned this user to department
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_by) REFERENCES users(id),
    UNIQUE KEY unique_user_department (user_id, department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng dự án (projects)
CREATE TABLE projects (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    department_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE', -- 'ACTIVE', 'COMPLETED', 'CANCELLED'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng quan hệ nhiều-nhiều giữa users và projects (user_projects)
CREATE TABLE user_projects (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    role VARCHAR(50) DEFAULT 'MEMBER', -- 'MEMBER', 'LEAD', 'COLLABORATOR'
    is_active BOOLEAN DEFAULT TRUE,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_by BIGINT NULL, -- Who assigned this user to project
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_by) REFERENCES users(id),
    UNIQUE KEY unique_user_project (user_id, project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng danh mục file theo phòng ban (department_categories)
CREATE TABLE department_categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    department_id BIGINT NOT NULL,
    color VARCHAR(7), -- HEX color code cho UI
    icon VARCHAR(50), -- Icon class name
    display_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(id),
    UNIQUE KEY unique_dept_category (department_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng loại file (file_types)
CREATE TABLE file_types (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL, -- 'DOCUMENT', 'IMAGE', 'VIDEO', 'SLIDE', 'SOURCE_CODE', 'OTHER'
    description TEXT,
    allowed_extensions JSON, -- ['pdf', 'doc', 'docx'] for DOCUMENT
    color VARCHAR(7), -- HEX color code
    icon VARCHAR(50), -- Icon class name
    max_size BIGINT, -- Max file size in bytes for this type
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng file (files)
CREATE TABLE files (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(255) UNIQUE NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    content_type VARCHAR(100) NOT NULL, -- MIME type
    file_hash VARCHAR(64) UNIQUE NOT NULL, -- SHA-256 hash để tránh duplicate
    
    -- Core classifications
    uploader_id BIGINT NOT NULL, -- Người upload file
    department_id BIGINT NOT NULL, -- Phòng ban sở hữu file
    department_category_id BIGINT NULL, -- Phân loại nội bộ của phòng ban
    file_type_id BIGINT NOT NULL, -- Loại file (Document, Image, Video, etc.)
    project_id BIGINT NULL, -- Dự án (nếu có)
    
    -- File metadata
    title VARCHAR(255), -- Tiêu đề tùy chỉnh
    description TEXT,
    tags JSON, -- Tags để tìm kiếm
    
    -- Sharing permissions
    visibility VARCHAR(20) DEFAULT 'PRIVATE', -- 'PRIVATE', 'DEPARTMENT', 'PUBLIC'
    
    -- System fields
    download_count BIGINT DEFAULT 0,
    is_deleted BOOLEAN DEFAULT FALSE, -- Soft delete
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    
    -- Google Drive integration (optional)
    drive_file_id VARCHAR(255), -- Google Drive file ID
    drive_folder_id VARCHAR(255), -- Google Drive folder ID
    
    FOREIGN KEY (uploader_id) REFERENCES users(id),
    FOREIGN KEY (department_id) REFERENCES departments(id),
    FOREIGN KEY (department_category_id) REFERENCES department_categories(id),
    FOREIGN KEY (file_type_id) REFERENCES file_types(id),
    FOREIGN KEY (project_id) REFERENCES projects(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng lịch sử truy cập file (file_access_logs)
CREATE TABLE file_access_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    file_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL, -- 'VIEW', 'DOWNLOAD', 'UPDATE', 'DELETE'
    ip_address VARCHAR(45), -- IPv4 và IPv6
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (file_id) REFERENCES files(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng chia sẻ file cá nhân (file_shares)
CREATE TABLE file_shares (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    file_id BIGINT NOT NULL,
    shared_by BIGINT NOT NULL, -- Người chia sẻ
    shared_with BIGINT NOT NULL, -- Người được chia sẻ
    permissions VARCHAR(20) DEFAULT 'read', -- 'read', 'write'
    expires_at TIMESTAMP NULL, -- Ngày hết hạn (optional)
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (file_id) REFERENCES files(id) ON DELETE CASCADE,
    FOREIGN KEY (shared_by) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (shared_with) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_file_share (file_id, shared_by, shared_with)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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

-- Indexes để tối ưu performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_is_active ON users(is_active);
CREATE INDEX idx_departments_parent_id ON departments(parent_id);
CREATE INDEX idx_departments_manager_id ON departments(manager_id);
CREATE INDEX idx_user_departments_user_id ON user_departments(user_id);
CREATE INDEX idx_user_departments_department_id ON user_departments(department_id);
CREATE INDEX idx_user_departments_is_active ON user_departments(is_active);
CREATE INDEX idx_user_projects_user_id ON user_projects(user_id);
CREATE INDEX idx_user_projects_project_id ON user_projects(project_id);
CREATE INDEX idx_user_projects_is_active ON user_projects(is_active);
CREATE INDEX idx_projects_department_id ON projects(department_id);
CREATE INDEX idx_projects_status ON projects(status);
CREATE INDEX idx_department_categories_dept_id ON department_categories(department_id);
CREATE INDEX idx_files_uploader_id ON files(uploader_id);
CREATE INDEX idx_files_department_id ON files(department_id);
CREATE INDEX idx_files_department_category_id ON files(department_category_id);
CREATE INDEX idx_files_file_type_id ON files(file_type_id);
CREATE INDEX idx_files_project_id ON files(project_id);
CREATE INDEX idx_files_hash ON files(file_hash);
CREATE INDEX idx_files_uploaded_at ON files(uploaded_at);
CREATE INDEX idx_files_visibility ON files(visibility);
CREATE INDEX idx_files_deleted ON files(is_deleted);
CREATE INDEX idx_file_access_logs_file_id ON file_access_logs(file_id);
CREATE INDEX idx_file_access_logs_user_id ON file_access_logs(user_id);
CREATE INDEX idx_file_access_logs_created_at ON file_access_logs(created_at);
CREATE INDEX idx_file_shares_file_id ON file_shares(file_id);
CREATE INDEX idx_file_shares_shared_with ON file_shares(shared_with);

-- JSON indexes cho MySQL 8.0+ (cho tags)
-- MySQL sử dụng functional indexes cho JSON
ALTER TABLE files ADD INDEX idx_files_tags_functional ((CAST(tags AS CHAR(255) ARRAY)));

-- Insert dữ liệu mặc định

-- Insert demo departments
INSERT INTO departments (name, description, is_active, created_at, updated_at) VALUES 
('IT Department', 'Information Technology and Software Development', true, NOW(), NOW()),
('Marketing Department', 'Marketing and Customer Relations', true, NOW(), NOW()),
('HR Department', 'Human Resources and Administration', true, NOW(), NOW()),
('Finance Department', 'Financial Management and Accounting', true, NOW(), NOW()),
('Operations Department', 'Business Operations and Process Management', true, NOW(), NOW());

-- Insert file types
INSERT INTO file_types (name, description, allowed_extensions, color, icon, max_size) VALUES 
('DOCUMENT', 'Documents (PDF, Word, Text)', '["pdf", "doc", "docx", "txt", "rtf"]', '#2196F3', 'document', 104857600),
('IMAGE', 'Images and Graphics', '["jpg", "jpeg", "png", "gif", "bmp", "svg"]', '#4CAF50', 'image', 52428800),
('VIDEO', 'Video and Multimedia', '["mp4", "avi", "mov", "wmv", "flv"]', '#FF9800', 'video', 524288000),
('SLIDE', 'Presentations', '["ppt", "pptx", "odp"]', '#9C27B0', 'presentation', 104857600),
('SOURCE_CODE', 'Source Code and Archives', '["zip", "rar", "7z", "tar", "gz", "java", "js", "css", "html", "py", "cpp"]', '#FF5722', 'code', 209715200),
('OTHER', 'Other File Types', '["csv", "xml", "json", "log"]', '#9E9E9E', 'file', 52428800);

-- Insert department categories for each department
INSERT INTO department_categories (name, description, department_id, color, icon, display_order) VALUES 
-- HR Department Categories
('Contracts', 'Employment contracts and agreements', 3, '#1976D2', 'contract', 1),
('Policies', 'Company policies and procedures', 3, '#388E3C', 'policy', 2),
('Recruitment', 'Recruitment documents and resumes', 3, '#F57C00', 'recruitment', 3),

-- IT Department Categories  
('Technical Documents', 'Technical specifications and documentation', 1, '#5D4037', 'tech-doc', 1),
('Meeting Minutes', 'Meeting notes and minutes', 1, '#7B1FA2', 'meeting', 2),
('Maintenance Requests', 'System maintenance and support requests', 1, '#D32F2F', 'maintenance', 3),

-- Marketing Department Categories
('Campaigns', 'Marketing campaigns and materials', 2, '#E91E63', 'campaign', 1),
('Research', 'Market research and analysis', 2, '#00796B', 'research', 2),
('Brand Assets', 'Brand guidelines and assets', 2, '#FF5722', 'brand', 3),

-- Finance Department Categories
('Budgets', 'Budget documents and financial plans', 4, '#2E7D32', 'budget', 1),
('Reports', 'Financial reports and statements', 4, '#1565C0', 'report', 2),
('Invoices', 'Invoices and billing documents', 4, '#E65100', 'invoice', 3),

-- Operations Department Categories
('Procedures', 'Operational procedures and workflows', 5, '#6A1B9A', 'procedure', 1),
('Quality Control', 'Quality assurance documents', 5, '#00838F', 'quality', 2),
('Training', 'Training materials and guides', 5, '#558B2F', 'training', 3);

INSERT INTO system_configs (config_key, config_value, data_type, description, is_public) VALUES 
('file.max-size', '104857600', 'NUMBER', 'Kích thước file tối đa (100MB)', true),
('file.allowed-types', '["pdf","doc","docx","txt","jpg","jpeg","png","gif","mp4","avi","mp3","wav","zip","rar","json","xml","csv","ppt","pptx"]', 'JSON', 'Các loại file được phép upload', true),
('storage.path', '/app/uploads', 'STRING', 'Đường dẫn lưu trữ file', false),
('app.name', 'File Management System', 'STRING', 'Tên ứng dụng', true),
('app.version', '1.0.0', 'STRING', 'Phiên bản ứng dụng', true);

-- Insert sample admin user for testing (password: admin123)
INSERT INTO users (username, email, password, full_name, role, is_active, created_at, updated_at) VALUES 
('admin', 'admin@filehub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM9lE5fWmBnWBoa.qR8y', 'System Administrator', 'ADMIN', true, NOW(), NOW()),
('user1', 'user1@filehub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM9lE5fWmBnWBoa.qR8y', 'Test User 1', 'USER', true, NOW(), NOW()),
('user2', 'user2@filehub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM9lE5fWmBnWBoa.qR8y', 'Test User 2', 'USER', true, NOW(), NOW()),
('hr_manager', 'hr@filehub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM9lE5fWmBnWBoa.qR8y', 'HR Manager', 'USER', true, NOW(), NOW()),
('finance_user', 'finance@filehub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM9lE5fWmBnWBoa.qR8y', 'Finance User', 'USER', true, NOW(), NOW());

-- Assign users to departments
INSERT INTO user_departments (user_id, department_id, role, is_active, assigned_at, assigned_by) VALUES 
-- Admin in IT Department as Manager
(1, 1, 'MANAGER', true, NOW(), 1),
-- Admin also in Marketing (multi-department user)
(1, 2, 'MANAGER', true, NOW(), 1),
-- User1 in IT Department
(2, 1, 'MEMBER', true, NOW(), 1),
-- User2 in Marketing Department
(3, 2, 'MEMBER', true, NOW(), 1),
-- HR Manager in HR Department
(4, 3, 'MANAGER', true, NOW(), 1),
-- Finance User in Finance Department
(5, 4, 'MEMBER', true, NOW(), 1),
-- User1 also in HR (cross-department collaboration)
(2, 3, 'MEMBER', true, NOW(), 1);

-- Update department managers
UPDATE departments SET manager_id = 1 WHERE id = 1; -- Admin manages IT
UPDATE departments SET manager_id = 1 WHERE id = 2; -- Admin manages Marketing (temp)

-- Insert demo projects
INSERT INTO projects (name, description, department_id, status, created_at, updated_at) VALUES 
('File Management System', 'Development of a comprehensive file management system with Vue.js and Spring Boot', 1, 'ACTIVE', NOW(), NOW()),
('Website Redesign', 'Complete redesign of company website with modern UI/UX', 2, 'ACTIVE', NOW(), NOW()),
('Employee Onboarding System', 'Digital transformation of employee onboarding process', 3, 'ACTIVE', NOW(), NOW()),
('Budget Management Tool', 'Internal tool for budget tracking and financial reporting', 4, 'ACTIVE', NOW(), NOW()),
('Process Optimization', 'Analysis and optimization of current business processes', 5, 'ACTIVE', NOW(), NOW());

-- Assign users to projects
INSERT INTO user_projects (user_id, project_id, role, is_active, assigned_at, assigned_by) VALUES 
-- Admin leads File Management System project
(1, 1, 'LEAD', true, NOW(), 1),
-- User1 works on File Management System
(2, 1, 'MEMBER', true, NOW(), 1),
-- Admin also leads Website Redesign
(1, 2, 'LEAD', true, NOW(), 1),
-- User2 works on Website Redesign
(3, 2, 'MEMBER', true, NOW(), 1),
-- HR Manager leads Employee Onboarding System
(4, 3, 'LEAD', true, NOW(), 1),
-- User1 collaborates on Employee Onboarding (cross-project work)
(2, 3, 'COLLABORATOR', true, NOW(), 1),
-- Finance User leads Budget Management Tool
(5, 4, 'LEAD', true, NOW(), 1),
-- Admin collaborates on Budget Management
(1, 5, 'MEMBER', true, NOW(), 1);