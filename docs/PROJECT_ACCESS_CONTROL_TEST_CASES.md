# Test Cases for Project-Based File Access Control

## Test Scenario Setup

This document outlines test cases to verify the new project-based file access control implementation.

## Prerequisites

1. Backend server running
2. Test users with different department/project assignments
3. Test files uploaded to departments and projects

## Test Data Setup

### Users
```
User A: Department 1, Project 1
User B: Department 1, Project 2  
User C: Department 1, No Projects
Admin: Admin role
```

### Files
```
File 1: Department 1, No Project
File 2: Department 1, Project 1
File 3: Department 1, Project 2
File 4: Public visibility
```

## Test Cases

### Test Case 1: Department Files Without Projects
**Scenario**: User C (Department 1, No Projects) tries to access File 1 (Department 1, No Project)
**Expected**: ✅ ALLOW - User can see department files that don't belong to projects
**API Endpoints to Test**:
- `GET /api/files/shared`
- `GET /api/files/shared/department/1`
- `GET /api/files/{file1-id}`

### Test Case 2: Project Files - Authorized Access
**Scenario**: User A (Department 1, Project 1) tries to access File 2 (Department 1, Project 1)
**Expected**: ✅ ALLOW - User can see files from their assigned projects
**API Endpoints to Test**:
- `GET /api/files/shared`
- `GET /api/files/shared/project/1`
- `GET /api/files/{file2-id}`

### Test Case 3: Project Files - Unauthorized Access (Same Department)
**Scenario**: User C (Department 1, No Projects) tries to access File 2 (Department 1, Project 1)
**Expected**: ❌ DENY - User cannot see project files they're not assigned to, even in same department
**API Endpoints to Test**:
- `GET /api/files/shared` (File 2 should not appear)
- `GET /api/files/shared/department/1` (File 2 should not appear)
- `GET /api/files/{file2-id}` (Should return 403/404)

### Test Case 4: Cross-Project Access Within Department
**Scenario**: User A (Department 1, Project 1) tries to access File 3 (Department 1, Project 2)
**Expected**: ❌ DENY - User cannot see files from other projects in same department
**API Endpoints to Test**:
- `GET /api/files/shared` (File 3 should not appear)
- `GET /api/files/shared/project/2` (Should return 403)
- `GET /api/files/{file3-id}` (Should return 403/404)

### Test Case 5: Public Files
**Scenario**: Any user tries to access File 4 (Public visibility)
**Expected**: ✅ ALLOW - Public files are accessible to all users
**API Endpoints to Test**:
- `GET /api/files/shared` (File 4 should appear for all users)
- `GET /api/files/{file4-id}` (Should work for all users)

### Test Case 6: Admin Access
**Scenario**: Admin tries to access any file
**Expected**: ✅ ALLOW - Admin can see all files regardless of department/project assignments
**API Endpoints to Test**:
- `GET /api/files/shared` (Should see all files)
- `GET /api/files/{any-file-id}` (Should work for all files)

### Test Case 7: Department Endpoint Behavior
**Scenario**: Call `GET /api/files/shared/department/1`
**Expected**: Should only return File 1 (department file without project), not File 2 or File 3 (project files)

## Manual Testing Steps

### Step 1: Verify Shared Files Endpoint
```bash
# Login as User C (Department 1, No Projects)
# Should see File 1 (department file) and File 4 (public file)
# Should NOT see File 2 or File 3 (project files)
GET /api/files/shared
```

### Step 2: Verify Department Endpoint
```bash
# Login as User C (Department 1, No Projects)
# Should see only File 1, not File 2 or File 3
GET /api/files/shared/department/1
```

### Step 3: Verify Project Endpoint Access Control
```bash
# Login as User C (Department 1, No Projects)
# Should return 403/error - user not assigned to project
GET /api/files/shared/project/1
```

### Step 4: Verify Individual File Access
```bash
# Login as User C (Department 1, No Projects)
# Try to access File 2 (Project 1 file)
# Should return 403/error
GET /api/files/{file2-id}
```

### Step 5: Verify File Download
```bash
# Login as User C (Department 1, No Projects)
# Try to download File 2 (Project 1 file)
# Should return 403/error
GET /api/files/{file2-id}/download
```

## Expected Behavior Summary

| User | File 1 (Dept) | File 2 (Proj 1) | File 3 (Proj 2) | File 4 (Public) |
|------|---------------|-----------------|-----------------|-----------------|
| User A (Dept 1, Proj 1) | ✅ | ✅ | ❌ | ✅ |
| User B (Dept 1, Proj 2) | ✅ | ❌ | ✅ | ✅ |
| User C (Dept 1, No Proj) | ✅ | ❌ | ❌ | ✅ |
| Admin | ✅ | ✅ | ✅ | ✅ |

## Success Criteria

- ✅ Users can access department files without projects
- ✅ Users can access files from their assigned projects
- ❌ Users cannot access project files they're not assigned to
- ✅ Public files are accessible to all users
- ✅ Admin can access all files
- ✅ Department endpoint excludes project files
- ✅ File access APIs respect the new access control logic

## Troubleshooting

If tests fail, check:
1. User assignments in `user_departments` and `user_projects` tables
2. File records have correct `department_id` and `project_id` values
3. Backend logs for authorization errors
4. Database constraints and foreign keys
