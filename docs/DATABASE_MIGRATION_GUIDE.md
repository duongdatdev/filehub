# Cập Nhật Database Schema Cho Admin API

## Tổng Quan Thay Đổi

Để đồng bộ với implementation Admin API đã tạo, database schema cần được cập nhật như sau:

## 1. Thay Đổi Chính

### 🔄 **Bảng Users - Cấu Trúc Mới**

#### Trước (Schema cũ):
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    role_id BIGINT NOT NULL,              -- ❌ Foreign key
    enabled BOOLEAN DEFAULT TRUE,         -- ❌ Spring Security field
    account_non_expired BOOLEAN DEFAULT TRUE,     -- ❌ Không sử dụng
    account_non_locked BOOLEAN DEFAULT TRUE,      -- ❌ Không sử dụng
    credentials_non_expired BOOLEAN DEFAULT TRUE, -- ❌ Không sử dụng
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id)   -- ❌ Foreign key constraint
);
```

#### Sau (Schema mới):
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',     -- ✅ Enum as string
    is_active BOOLEAN DEFAULT TRUE,               -- ✅ Simplified status
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 🗑️ **Bảng Roles - Đã Xóa**
- Không cần bảng `roles` riêng biệt
- Sử dụng `Role` enum trong Java: `USER`, `ADMIN`
- Lưu trực tiếp trong cột `role` của bảng `users`

## 2. Lý Do Thay Đổi

### ✅ **Ưu Điểm Của Schema Mới:**
1. **Đơn giản hóa**: Không cần join với bảng roles
2. **Hiệu suất**: Truy vấn nhanh hơn, ít join
3. **Bảo trì**: Dễ dàng thêm/sửa roles trong enum
4. **Consistency**: Khớp với implementation Java

### 📊 **So Sánh Performance:**
```sql
-- Trước (có join):
SELECT u.*, r.name as role_name 
FROM users u 
JOIN roles r ON u.role_id = r.id 
WHERE u.is_active = true;

-- Sau (không join):
SELECT * 
FROM users 
WHERE is_active = true;
```

## 3. Cách Cập Nhật

### 🆕 **Database Mới:**
- Sử dụng file `schema.sql` đã được cập nhật
- Chạy trực tiếp để tạo database từ đầu

### 🔄 **Database Hiện Có:**
- Sử dụng file `migration_to_admin_api.sql`
- Backup database trước khi chạy migration
- Chạy từng bước một để đảm bảo an toàn

```bash
# Backup database
mysqldump -u root -p filehub_db > backup_before_migration.sql

# Chạy migration
mysql -u root -p filehub_db < migration_to_admin_api.sql
```

## 4. Dữ Liệu Mẫu

### 👤 **Test Users:**
```sql
-- Admin user (password: admin123)
username: admin
email: admin@filehub.com
role: ADMIN
is_active: true

-- Regular users
username: user1, user2
role: USER
is_active: true/false
```

## 5. API Endpoints Tương Ứng

### 🔐 **Admin APIs:**
```
GET /api/admin/users                    - Lấy danh sách users
GET /api/admin/users/{id}              - Lấy thông tin user
PATCH /api/admin/users/{id}/status     - Cập nhật trạng thái user
```

### 📝 **Request Examples:**
```bash
# Login admin
POST /api/auth/login
{
  "username": "admin",
  "password": "admin123"
}

# Get users with filter
GET /api/admin/users?role=USER&isActive=true&page=0&size=10
```

## 6. Validation & Testing

### ✅ **Checklist:**
- [ ] Database schema đã cập nhật
- [ ] Migration chạy thành công
- [ ] Admin user có thể login
- [ ] API `/api/admin/users` hoạt động
- [ ] Filtering và pagination hoạt động
- [ ] Frontend có thể kết nối API

### 🧪 **Test Commands:**
```bash
# Test login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Test get users (với token)
curl -X GET "http://localhost:8080/api/admin/users" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 7. Lưu Ý Quan Trọng

### ⚠️ **Cảnh Báo:**
1. **Backup Data**: Luôn backup trước khi migration
2. **Test Environment**: Test migration trên dev trước
3. **Downtime**: Migration có thể cần downtime
4. **Foreign Keys**: Kiểm tra ràng buộc với bảng khác

### 🔧 **Troubleshooting:**
```sql
-- Nếu có lỗi foreign key:
SET FOREIGN_KEY_CHECKS = 0;
-- Chạy migration
SET FOREIGN_KEY_CHECKS = 1;

-- Kiểm tra cấu trúc sau migration:
DESCRIBE users;
SHOW INDEXES FROM users;
```

## 8. Next Steps

1. ✅ Cập nhật database schema
2. ✅ Test Admin API endpoints  
3. 🔄 Cập nhật frontend để sử dụng API mới
4. 🔄 Thêm validation và error handling
5. 🔄 Deploy lên production environment

---

**📧 Contact**: Nếu có vấn đề trong quá trình migration, hãy kiểm tra logs và backup data trước khi thử lại.
