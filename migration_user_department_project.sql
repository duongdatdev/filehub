-- Migration script for updating existing database to support many-to-many relationships
-- between users-departments and users-projects
-- Date: 2025-07-26

-- Step 1: Create user_departments table
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

-- Step 2: Create user_projects table
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

-- Step 3: Migrate existing user-department relationships
INSERT INTO user_departments (user_id, department_id, role, is_active, assigned_at, assigned_by)
SELECT 
    u.id as user_id,
    u.department_id,
    CASE 
        WHEN d.manager_id = u.id THEN 'MANAGER'
        ELSE 'MEMBER'
    END as role,
    u.is_active,
    u.created_at,
    1 -- Assume admin (id=1) assigned them
FROM users u
LEFT JOIN departments d ON u.department_id = d.id
WHERE u.department_id IS NOT NULL;

-- Step 4: Create indexes for new tables
CREATE INDEX idx_user_departments_user_id ON user_departments(user_id);
CREATE INDEX idx_user_departments_department_id ON user_departments(department_id);
CREATE INDEX idx_user_departments_is_active ON user_departments(is_active);
CREATE INDEX idx_user_projects_user_id ON user_projects(user_id);
CREATE INDEX idx_user_projects_project_id ON user_projects(project_id);
CREATE INDEX idx_user_projects_is_active ON user_projects(is_active);

-- Step 5: Remove old department_id column from users table (OPTIONAL - DO THIS CAREFULLY!)
-- Uncomment the following lines ONLY after verifying the migration worked correctly
-- and you have a backup of your database

-- First, drop the foreign key constraint
-- ALTER TABLE users DROP FOREIGN KEY users_ibfk_1; -- This might have a different name, check with SHOW CREATE TABLE users;

-- Then drop the index
-- DROP INDEX idx_users_department_id ON users;

-- Finally, drop the column
-- ALTER TABLE users DROP COLUMN department_id;

-- Step 6: Add file upload validation logic (Application level - for reference)
-- NOTE: This validation should be implemented in your Spring Boot application
-- 
-- File upload validation rules:
-- 1. User can upload files to departments they belong to (user_departments.is_active = true)
-- 2. User can upload files to projects they belong to (user_projects.is_active = true)
-- 3. Admin users can upload files to any department/project
-- 4. When uploading to a department, the file.department_id must match user's departments
-- 5. When uploading to a project, the file.project_id must match user's projects

-- Example validation query for department upload:
-- SELECT COUNT(*) FROM user_departments ud 
-- WHERE ud.user_id = ? AND ud.department_id = ? AND ud.is_active = true;

-- Example validation query for project upload:
-- SELECT COUNT(*) FROM user_projects up 
-- WHERE up.user_id = ? AND up.project_id = ? AND up.is_active = true;

-- Step 7: Update sample data (if needed)
-- Add some sample project assignments
INSERT INTO user_projects (user_id, project_id, role, is_active, assigned_at, assigned_by) 
SELECT 1, p.id, 'LEAD', true, NOW(), 1 
FROM projects p 
WHERE p.id IN (1, 2) -- Admin leads first two projects
ON DUPLICATE KEY UPDATE role = 'LEAD';

INSERT INTO user_projects (user_id, project_id, role, is_active, assigned_at, assigned_by) 
SELECT 2, p.id, 'MEMBER', true, NOW(), 1 
FROM projects p 
WHERE p.id = 1 -- User1 works on File Management System
ON DUPLICATE KEY UPDATE role = 'MEMBER';

-- Verification queries
-- Check user-department assignments:
-- SELECT u.username, u.full_name, d.name as department, ud.role 
-- FROM users u 
-- JOIN user_departments ud ON u.id = ud.user_id 
-- JOIN departments d ON ud.department_id = d.id 
-- WHERE ud.is_active = true;

-- Check user-project assignments:
-- SELECT u.username, u.full_name, p.name as project, up.role 
-- FROM users u 
-- JOIN user_projects up ON u.id = up.user_id 
-- JOIN projects p ON up.project_id = p.id 
-- WHERE up.is_active = true;
