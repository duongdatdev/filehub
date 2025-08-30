# FileHub Docker Setup

## Tổng quan

Dự án FileHub sử dụng Docker Compose để chạy:
- **Backend**: Spring Boot (Java 21) với MySQL
- **Frontend**: Vue.js với Nginx
- **Database**: MySQL 8.0

## Cấu trúc File Docker

- `docker-compose.yml`: Orchestrates tất cả services
- `filehub-back/Dockerfile`: Backend container configuration
- `filehub-front/Dockerfile`: Frontend container configuration  
- `filehub-front/nginx.conf`: Nginx configuration cho frontend
- `filehub-back/src/main/resources/application-docker.properties`: Spring properties cho Docker

## Cách chạy dự án

### 1. Chuẩn bị môi trường

Đảm bảo bạn đã cài đặt:
- Docker Desktop
- Docker Compose

### 2. Chạy dự án

```bash
# Clone repository và di chuyển vào thư mục
cd d:\DoAn\tttt

# Build và chạy tất cả services
docker-compose up --build

# Hoặc chạy ở background
docker-compose up --build -d
```

### 3. Truy cập ứng dụng

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **MySQL**: localhost:3307 (port 3307 để tránh conflict với MySQL local)

### 4. Dừng dự án

```bash
# Dừng và xóa containers
docker-compose down

# Dừng và xóa containers + volumes (xóa database)
docker-compose down -v
```

## Services Configuration

### MySQL Database
- Port: 3307 (external) → 3306 (internal)
- Database: `filehub_db`
- Username: `root`
- Password: `secret`
- Volume: `mysql_data` (persistent storage)

### Backend (Spring Boot)
- Port: 8080
- Profile: `docker`
- Upload directory: `/app/uploads` (mounted from host)
- Tokens directory: `/app/tokens` (mounted from host)

### Frontend (Vue.js + Nginx)
- Port: 3000 (external) → 80 (internal)
- Nginx proxy `/api/` requests to backend
- Supports Vue Router history mode

## Development Commands

```bash
# Xem logs của tất cả services
docker-compose logs -f

# Xem logs của service cụ thể
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f mysql

# Restart một service
docker-compose restart backend

# Chạy lại chỉ một service
docker-compose up --build backend

# Access vào container
docker-compose exec backend bash
docker-compose exec mysql mysql -u root -p filehub_db
```

## Troubleshooting

### 1. MySQL connection issues
- Đảm bảo MySQL service đã healthy trước khi backend start
- Check logs: `docker-compose logs mysql`

### 2. Frontend không load được
- Check nginx configuration
- Verify backend API endpoints

### 3. File upload issues
- Đảm bảo volumes được mount đúng
- Check permissions của thư mục uploads

### 4. Port conflicts
- Thay đổi ports trong docker-compose.yml nếu bị conflict
- Default ports: 3000 (frontend), 8080 (backend), 3307 (mysql)

## Environment Variables

Bạn có thể tùy chỉnh thông qua environment variables trong docker-compose.yml:

```yaml
environment:
  - MYSQL_ROOT_PASSWORD=your_password
  - SPRING_DATASOURCE_USERNAME=your_username
  - SPRING_DATASOURCE_PASSWORD=your_password
```
