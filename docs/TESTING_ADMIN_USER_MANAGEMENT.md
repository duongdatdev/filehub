# Testing Admin User Management Features

## Prerequisites
- Backend server running on http://localhost:8080
- Admin user credentials for authentication
- REST client (Postman, curl, or similar)

## Authentication
First, authenticate as an admin user to get an access token:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "your_admin_password"
  }'
```

Use the returned `accessToken` in the Authorization header for subsequent requests:
```
Authorization: Bearer YOUR_ACCESS_TOKEN
```

## Testing New Endpoints

### 1. Get User Details with Assignments
```bash
curl -X GET http://localhost:8080/api/admin/users/1/details \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 2. Assign User to Department
```bash
curl -X POST http://localhost:8080/api/admin/users/1/department \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "departmentId": 1,
    "role": "MEMBER"
  }'
```

### 3. Assign User to Project
```bash
curl -X POST http://localhost:8080/api/admin/users/1/projects \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "projectIds": [1],
    "role": "MEMBER"
  }'
```

### 4. Update User's Department Role
```bash
curl -X PUT "http://localhost:8080/api/admin/users/1/departments/1/role?role=MANAGER" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 5. Update User's Project Role
```bash
curl -X PUT "http://localhost:8080/api/admin/users/1/projects/1/role?role=LEAD" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 6. Remove User from Department
```bash
curl -X DELETE http://localhost:8080/api/admin/users/1/departments/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 7. Remove User from Project
```bash
curl -X DELETE http://localhost:8080/api/admin/users/1/projects/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 8. Get User's Departments
```bash
curl -X GET http://localhost:8080/api/admin/users/1/departments \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 9. Get User's Projects
```bash
curl -X GET http://localhost:8080/api/admin/users/1/projects \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 10. Batch Update User Assignments
```bash
curl -X POST http://localhost:8080/api/admin/users/batch-update \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "userIds": [1, 2, 3],
    "departmentId": 1,
    "operation": "ADD",
    "role": "MEMBER"
  }'
```

### 11. Bulk Assign Users
```bash
curl -X POST http://localhost:8080/api/admin/users/bulk-assign \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '[
    {
      "userId": 1,
      "departmentId": 1,
      "role": "MEMBER"
    },
    {
      "userId": 2,
      "projectId": 1,
      "role": "COLLABORATOR"
    }
  ]'
```

## Expected Responses

### Success Response Format
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    // Response data specific to the endpoint
  }
}
```

### Error Response Format
```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

### User Detail Response Example
```json
{
  "success": true,
  "message": "User details with assignments retrieved successfully",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "fullName": "Test User",
    "role": "USER",
    "isActive": true,
    "createdAt": "2024-01-01T10:00:00",
    "departments": [
      {
        "id": 1,
        "userId": 1,
        "departmentId": 1,
        "role": "MEMBER",
        "isActive": true,
        "assignedAt": "2024-01-01T10:00:00"
      }
    ],
    "projects": [
      {
        "id": 1,
        "userId": 1,
        "projectId": 1,
        "role": "LEAD",
        "isActive": true,
        "assignedAt": "2024-01-01T10:00:00"
      }
    ]
  }
}
```

## Testing Notes

1. **User IDs**: Replace `1` in the examples with actual user IDs from your database
2. **Department/Project IDs**: Replace with actual department and project IDs
3. **Roles**: Valid roles include: `MEMBER`, `MANAGER`, `LEAD`, `COLLABORATOR`
4. **Batch Operations**: Support operations: `ADD`, `REMOVE`, `UPDATE_ROLE`
5. **Authorization**: All endpoints require ADMIN role
6. **Error Handling**: Endpoints return appropriate error messages for invalid requests

## Verification

After testing assignments, verify the changes by:
1. Checking the user details endpoint
2. Viewing individual user departments/projects
3. Testing file upload permissions based on new assignments
4. Verifying role-based access controls work correctly
