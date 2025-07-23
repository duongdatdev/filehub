# Test Admin User API

## 1. Login as admin to get JWT token
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

### Expected Response:
# {
#   "success": true,
#   "message": "Login successful",
#   "data": {
#     "id": 1,
#     "username": "admin",
#     "email": "admin@filehub.com",
#     "fullName": "System Administrator",
#     "role": "ADMIN",
#     "isActive": true,
#     "createdAt": "...",
#     "accessToken": "eyJ...",
#     "refreshToken": "eyJ..."
#   }
# }

## 2. Get all users (use the access token from step 1)
GET http://localhost:8080/api/admin/users
Authorization: Bearer <ACCESS_TOKEN>

### Expected Response:
# {
#   "success": true,
#   "message": "Users retrieved successfully",
#   "data": {
#     "content": [...],
#     "pageNumber": 0,
#     "pageSize": 10,
#     "totalElements": 11,
#     "totalPages": 2,
#     "isFirst": true,
#     "isLast": false,
#     "hasNext": true,
#     "hasPrevious": false
#   }
# }

## 3. Get users with filters
GET http://localhost:8080/api/admin/users?role=USER&isActive=true&page=0&size=5
Authorization: Bearer <ACCESS_TOKEN>

## 4. Get specific user by ID
GET http://localhost:8080/api/admin/users/1
Authorization: Bearer <ACCESS_TOKEN>

## 5. Update user status
PATCH http://localhost:8080/api/admin/users/2/status
Content-Type: application/json
Authorization: Bearer <ACCESS_TOKEN>

{
  "isActive": false
}

## Test Commands (using curl)

### 1. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### 2. Get users (replace <TOKEN> with actual token)
```bash
curl -X GET http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer <TOKEN>"
```

### 3. Get users with filters
```bash
curl -X GET "http://localhost:8080/api/admin/users?role=USER&isActive=true&page=0&size=5" \
  -H "Authorization: Bearer <TOKEN>"
```

### 4. Get user by ID
```bash
curl -X GET http://localhost:8080/api/admin/users/1 \
  -H "Authorization: Bearer <TOKEN>"
```

### 5. Update user status
```bash
curl -X PATCH "http://localhost:8080/api/admin/users/2/status" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"isActive": false}'
```
