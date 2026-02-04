# User Role Change Feature

## Overview
Added functionality to allow administrators to change a user's main role between USER and ADMIN directly from the admin user management interface.

## Backend Changes

### 1. New Request DTO
**File**: `filehub-back/src/main/java/com/duongdat/filehub/dto/request/UpdateUserRoleRequest.java`
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRoleRequest {
    @NotNull(message = "role field is required")
    private Role role;
}
```

### 2. New API Endpoint
**Endpoint**: `PATCH /api/admin/users/{id}/role`
**Method**: `AdminController.updateUserRole()`
**Authorization**: Requires ADMIN role
**Request Body**: 
```json
{
  "role": "USER" | "ADMIN"
}
```
**Response**: Returns updated UserResponse

### 3. Service Method
**Method**: `AdminService.updateUserRole(Long id, Role role)`
- Updates the user's main role in the database
- Returns the updated user information

### 4. Test Coverage
Added test case `AdminControllerUserManagementTest.testUpdateUserRole()` to verify the endpoint functionality.

## Frontend Changes

### 1. API Service
**File**: `filehub-front/src/services/adminApi.ts`
- Added `UpdateUserRoleRequest` interface
- Added `updateUserRole(id: number, role: 'USER' | 'ADMIN')` method

### 2. Store Integration
**File**: `filehub-front/src/stores/admin.ts`
- Added `updateUserRole()` method to the admin store
- Updates local state after successful role change
- Handles error states and loading

### 3. UI Component
**File**: `filehub-front/src/components/AdminUsersTable.vue`
- Added role dropdown in the actions column for each user
- Displays different colors for USER (gray) and ADMIN (purple) roles
- Shows loading spinner while role is being updated
- Prevents multiple simultaneous updates

## Features

### Security
- **Authorization**: Only ADMIN users can change roles
- **Validation**: Request body validation ensures valid role values
- **Error Handling**: Proper error responses and frontend error handling

### User Experience
- **Visual Feedback**: Role dropdown shows current role with color coding
- **Loading States**: Displays loading spinner during role updates
- **Real-time Updates**: UI updates immediately after successful role change
- **Error Handling**: Shows error messages if role change fails

### Role Types
- **USER**: Standard user with basic permissions
- **ADMIN**: Administrator with full system access

## Usage

### Frontend
1. Navigate to `/admin/users` page (requires admin login)
2. In the users table, each row has a role dropdown in the actions column
3. Select the desired role (USER or ADMIN) from the dropdown
4. The role change will be applied immediately with visual feedback

### API Usage
```bash
# Example: Change user with ID 123 to ADMIN role
PATCH /api/admin/users/123/role
Content-Type: application/json
Authorization: Bearer <admin-jwt-token>

{
  "role": "ADMIN"
}
```

## Error Handling
- Returns 401 if user is not authenticated
- Returns 403 if user is not an admin
- Returns 400 if request body is invalid
- Returns 404 if user is not found
- Frontend displays error messages and maintains previous state on failure

## Integration
This feature integrates seamlessly with the existing user management system and maintains consistency with the current authentication and authorization patterns used throughout the application.
