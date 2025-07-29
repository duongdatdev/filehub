# Project-Based File Access Control Enhancement

## Overview

This enhancement implements strict project-based file access control to ensure that files uploaded to projects are only visible to users who are explicitly assigned to those projects, even if they are members of the same department.

## Problem Statement

Previously, the system allowed users to see all files in their department, including files that belonged to projects they weren't assigned to. This created a security issue where department members could access project-specific files without proper authorization.

## Solution

The new access control logic prioritizes project-level permissions over department-level permissions for files that belong to projects.

## New Access Control Rules

### File Visibility Rules

1. **Public Files**: Visible to all users regardless of department/project assignments
2. **Project Files**: Only visible to users explicitly assigned to that project
3. **Department Files (non-project)**: Visible to all users assigned to the department
4. **User's Own Files**: Always visible to the file owner
5. **Admin Access**: Administrators can access all files

### Technical Implementation

#### Repository Query Changes

The authorization logic has been updated in the repository queries:

**Before (insecure):**
```sql
(f.departmentId IN :accessibleDepartmentIds OR 
 f.projectId IN :accessibleProjectIds OR 
 f.visibility = 'PUBLIC')
```

**After (secure):**
```sql
(f.visibility = 'PUBLIC' OR 
 (f.projectId IS NOT NULL AND f.projectId IN :accessibleProjectIds) OR 
 (f.projectId IS NULL AND f.departmentId IN :accessibleDepartmentIds))
```

#### Affected Components

1. **FileRepository**
   - `findFilesWithAuthorizationFilters`: Updated query logic
   - `findSharedFilesWithAuthorizationFilters`: Updated query logic
   - `findSharedFilesByDepartment`: Now excludes project files (`f.projectId IS NULL`)
   - `findByDepartmentIdAndProjectIdIsNullAndIsDeletedFalse`: New method for department-only files

2. **FileService**
   - `getFileById`: Updated access control logic
   - `downloadFile`: Updated access control logic
   - `getFilesByDepartment`: Now only returns non-project files
   - `getUserFiles`: Uses updated repository queries
   - `getSharedFiles`: Uses updated repository queries

## Usage Examples

### Scenario 1: Department Member Without Project Access
- **User**: John (Marketing Department)
- **Project**: "Secret Campaign" (Marketing Department)
- **File**: campaign-strategy.pdf (uploaded to "Secret Campaign" project)
- **Result**: John CANNOT see this file unless he's assigned to the "Secret Campaign" project

### Scenario 2: Department Files Without Projects
- **User**: John (Marketing Department)
- **File**: department-guidelines.pdf (uploaded to Marketing Department, no project)
- **Result**: John CAN see this file as it's a department-level file

### Scenario 3: Multi-Department Project
- **User**: Alice (Marketing Department)
- **Project**: "Cross-Functional Initiative" (involves Marketing + Sales)
- **File**: project-plan.pdf (uploaded to "Cross-Functional Initiative")
- **Result**: Alice CAN see this file if she's assigned to the project, regardless of her department

## API Behavior Changes

### GET /api/files/shared/department/{departmentId}
- **Before**: Returns all files in the department (including project files)
- **After**: Returns only department files that don't belong to projects

### GET /api/files/shared/project/{projectId}
- **Before**: Returns project files if user is in the department
- **After**: Returns project files only if user is assigned to the project

### GET /api/files/shared
- **Before**: Shows files from accessible departments (including all project files in those departments)
- **After**: Shows only files user has explicit access to (department files without projects + assigned project files)

## Migration Notes

### Backward Compatibility
- All existing API endpoints continue to work
- No changes to request/response formats
- Only the underlying access control logic has changed

### Security Improvements
- Eliminates unauthorized access to project files
- Maintains proper department-level collaboration for non-project files
- Preserves admin and owner access rights

### Performance Considerations
- New queries are optimized with proper indexing
- No significant performance impact expected
- Query complexity remains similar

## Testing Guidelines

### Test Cases to Verify

1. **Project Access Control**
   - User in department but not in project cannot see project files
   - User assigned to project can see project files
   - User removed from project loses access to project files

2. **Department Access Control**
   - User in department can see department files without projects
   - User not in department cannot see department files

3. **Mixed Scenarios**
   - User in multiple departments sees correct files
   - User in multiple projects sees correct files
   - Files moved between projects maintain proper access control

4. **Admin Access**
   - Admin can see all files regardless of assignments
   - Admin access works for both API endpoints and direct file access

## Configuration

No additional configuration is required. The changes are implemented at the application logic level and will take effect immediately upon deployment.

## Benefits

1. **Enhanced Security**: Project files are properly isolated
2. **Flexible Collaboration**: Department-level sharing still works for non-project files
3. **Compliance**: Better adherence to principle of least privilege
4. **Audit Trail**: Clear separation between department and project access
5. **Scalability**: Supports complex multi-department projects

## Future Enhancements

- Role-based access within projects (project members vs project leads)
- Time-based access controls for temporary project assignments
- File-level access controls independent of department/project
- Audit logging for access control violations
