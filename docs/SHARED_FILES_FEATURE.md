# Shared Files Feature - User Access to Department and Project Files

## Overview

This feature allows users to view and access files uploaded by other users within their assigned departments and projects. Users can now see all files they have access to, not just their own uploads.

## Business Rules

### Access Permissions

1. **Own Files**: Users can always view, download, and delete their own uploaded files
2. **Department Files (non-project)**: Users can view and download files uploaded to departments they are assigned to, but only if those files don't belong to specific projects
3. **Project Files**: Users can view and download files uploaded to projects they are assigned to, regardless of department membership
4. **Public Files**: All users can view and download files marked as "PUBLIC"
5. **Admin Access**: Administrators can access all files regardless of department/project assignments

### Important Note on Project-Based Access Control

**Files uploaded to projects are ONLY visible to users assigned to those specific projects**, even if other users are in the same department. This ensures project-level security and prevents unauthorized access to project-specific files.

Example:
- User A: Marketing Department, Project Alpha
- User B: Marketing Department, Project Beta  
- User C: Marketing Department, No Projects
- File X: Uploaded to Marketing Department, Project Alpha

Result: Only User A can see File X, even though all users are in Marketing Department.

### File Operations

- **View/List**: Users can see files from accessible departments and projects
- **Download**: Users can download files they have access to
- **Delete**: Only file owners and administrators can delete files
- **Upload**: Users can only upload to departments/projects they are assigned to (existing restriction)

## New API Endpoints

### 1. Get Shared Files (All accessible files)
```
GET /api/files/shared
```
**Parameters:**
- `filename` (optional): Filter by filename
- `departmentCategoryId` (optional): Filter by department category
- `departmentId` (optional): Filter by department
- `projectId` (optional): Filter by project
- `fileTypeId` (optional): Filter by file type
- `contentType` (optional): Filter by content type
- `page`, `size`, `sortBy`, `sortDirection`: Pagination parameters

**Returns:** Paginated list of all files the user can access (from own uploads, department files, project files, and public files)

### 2. Get Shared Files by Department
```
GET /api/files/shared/department/{departmentId}
```
**Parameters:**
- `departmentId`: Department ID
- Same filtering and pagination parameters as above

**Returns:** Paginated list of all files in the specified department (if user has access)

### 3. Get Shared Files by Project
```
GET /api/files/shared/project/{projectId}
```
**Parameters:**
- `projectId`: Project ID
- Same filtering and pagination parameters as above

**Returns:** Paginated list of all files in the specified project (if user has access)

### 4. Admin: Get All Shared Files
```
GET /api/admin/files/shared
```
**Admin-only endpoint** that returns all files in the system with advanced filtering options.

## Updated Existing Endpoints

### File Access (GET /api/files/{id})
- Now allows access to files from user's accessible departments and projects
- Previously only allowed access to user's own files

### File Download (GET /api/files/{id}/download)
- Now allows downloading files from user's accessible departments and projects
- Previously only allowed downloading user's own files

### File Delete (DELETE /api/files/{id})
- Still restricted to file owners and administrators
- No change in behavior

## Technical Implementation

### Repository Changes
- Added `findSharedFilesWithAuthorizationFilters`: Gets all files (from any user) with authorization filtering
- Added `findSharedFilesByDepartment`: Gets all files in a specific department
- Added `findSharedFilesByProject`: Gets all files in a specific project

### Service Layer Changes
- Enhanced permission checking in `FileService`
- Added methods for shared file access with proper authorization
- Updated file access logic to check department/project membership

### Authorization Logic
The system checks access permissions in this order:
1. **Admin**: Full access to all files
2. **File Owner**: Access to own files
3. **Public Files**: Access to files marked as public visibility
4. **Project Files**: Access to files in assigned projects (takes priority over department membership)
5. **Department Files (non-project)**: Access to department files that don't belong to specific projects

## Security Considerations

1. **Authorization**: All file access is validated against user's department and project assignments
2. **Ownership**: File deletion remains restricted to owners and administrators
3. **Visibility**: Files respect visibility settings (PRIVATE, DEPARTMENT, PROJECT, PUBLIC)
4. **Audit Trail**: File access is still logged for security purposes

## Frontend Integration

The frontend can now use these new endpoints to:
1. Show a "Shared Files" or "Team Files" section
2. Display files from colleagues in the same departments/projects
3. Provide better collaboration features
4. Implement department and project file browsers

## Example Usage

### User in Marketing Department
- Can see files uploaded by other Marketing department members
- Can download and view these files
- Cannot delete files uploaded by others
- Can still upload files to Marketing department

### User in Multiple Projects
- Can see files from all assigned projects
- Can collaborate by accessing project files from teammates
- Files are organized by project for better navigation

### Department Manager
- Can see all files in their managed department
- Can see files from their project assignments
- Still cannot delete files they didn't upload (unless admin)

## Migration Notes

- **Backward Compatibility**: All existing API endpoints continue to work as before
- **Performance**: New queries are optimized with proper indexing on department_id and project_id
- **Security**: No security regression - users can only see files they should have access to based on their assignments
