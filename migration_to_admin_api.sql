-- Migration script để cập nhật database hiện có cho Admin API
-- Chạy script này nếu bạn đã có database với schema cũ

-- 1. Thêm cột role (enum) vào bảng users
ALTER TABLE users ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER';

-- 2. Thêm cột is_active thay thế cho enabled
ALTER TABLE users ADD COLUMN is_active BOOLEAN DEFAULT TRUE;

-- 3. Cập nhật dữ liệu role từ bảng roles (nếu có)
UPDATE users u 
SET role = (
    SELECT r.name 
    FROM roles r 
    WHERE r.id = u.role_id
) 
WHERE EXISTS (SELECT 1 FROM roles r WHERE r.id = u.role_id);

-- 4. Cập nhật is_active từ enabled
UPDATE users SET is_active = enabled WHERE enabled IS NOT NULL;

-- 5. Xóa các cột không cần thiết
ALTER TABLE users DROP FOREIGN KEY users_ibfk_1; -- Xóa foreign key constraint
ALTER TABLE users DROP COLUMN role_id;
ALTER TABLE users DROP COLUMN enabled;
ALTER TABLE users DROP COLUMN account_non_expired;
ALTER TABLE users DROP COLUMN account_non_locked;
ALTER TABLE users DROP COLUMN credentials_non_expired;

-- 6. Xóa bảng roles (không cần thiết nữa)
DROP TABLE IF EXISTS roles;

-- 7. Thêm indexes mới
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_is_active ON users(is_active);

-- 8. Xóa index cũ nếu tồn tại
DROP INDEX IF EXISTS idx_users_role_id ON users;

-- 9. Insert admin user mẫu nếu chưa có (password: admin123)
INSERT IGNORE INTO users (username, email, password, full_name, role, is_active, created_at, updated_at) VALUES 
('admin', 'admin@filehub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM9lE5fWmBnWBoa.qR8y', 'System Administrator', 'ADMIN', true, NOW(), NOW());

-- 10. Verify the changes
SELECT 'Migration completed successfully. Users table structure:' as message;
DESCRIBE users;
SELECT 'Sample data:' as message;
SELECT id, username, email, full_name, role, is_active FROM users LIMIT 5;
