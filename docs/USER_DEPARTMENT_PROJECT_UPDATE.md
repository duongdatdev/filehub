# User-Department-Project Relationship Update

## Overview
The database schema has been updated to support many-to-many relationships between users and departments, and between users and projects. This allows users to belong to multiple departments and work on multiple projects simultaneously.

## Schema Changes

### New Tables

#### 1. `user_departments`
- **Purpose**: Many-to-many relationship between users and departments
- **Key Fields**:
  - `user_id`: Reference to user
  - `department_id`: Reference to department
  - `role`: User's role in department ('MEMBER', 'MANAGER', 'DEPUTY_MANAGER')
  - `is_active`: Whether the assignment is currently active
  - `assigned_at`: When the user was assigned to department
  - `assigned_by`: Who made the assignment

#### 2. `user_projects`
- **Purpose**: Many-to-many relationship between users and projects
- **Key Fields**:
  - `user_id`: Reference to user
  - `project_id`: Reference to project
  - `role`: User's role in project ('MEMBER', 'LEAD', 'COLLABORATOR')
  - `is_active`: Whether the assignment is currently active
  - `assigned_at`: When the user was assigned to project
  - `assigned_by`: Who made the assignment

### Modified Tables

#### `users` table
- **Removed**: `department_id` column (replaced by `user_departments` table)
- **Benefit**: Users can now belong to multiple departments

## File Upload Restrictions

### New Business Rules

1. **Department-based Upload**:
   - Users can only upload files to departments they are assigned to
   - Check: `user_departments.is_active = true`
   - Query: 
   ```sql
   SELECT COUNT(*) FROM user_departments ud 
   WHERE ud.user_id = ? AND ud.department_id = ? AND ud.is_active = true
   ```

2. **Project-based Upload**:
   - Users can only upload files to projects they are assigned to
   - Check: `user_projects.is_active = true`
   - Query:
   ```sql
   SELECT COUNT(*) FROM user_projects up 
   WHERE up.user_id = ? AND up.project_id = ? AND up.is_active = true
   ```

3. **Admin Override**:
   - Admin users (role = 'ADMIN') can upload to any department or project
   - No additional checks needed for admin users

### Implementation in Spring Boot

#### File Upload Validation Service

```java
@Service
public class FileUploadValidationService {
    
    @Autowired
    private UserDepartmentRepository userDepartmentRepository;
    
    @Autowired
    private UserProjectRepository userProjectRepository;
    
    public boolean canUploadToDepartment(Long userId, Long departmentId, String userRole) {
        // Admin can upload anywhere
        if ("ADMIN".equals(userRole)) {
            return true;
        }
        
        // Check if user is assigned to department
        return userDepartmentRepository.existsByUserIdAndDepartmentIdAndIsActive(
            userId, departmentId, true);
    }
    
    public boolean canUploadToProject(Long userId, Long projectId, String userRole) {
        // Admin can upload anywhere
        if ("ADMIN".equals(userRole)) {
            return true;
        }
        
        // Check if user is assigned to project
        return userProjectRepository.existsByUserIdAndProjectIdAndIsActive(
            userId, projectId, true);
    }
    
    public List<Department> getUserDepartments(Long userId) {
        return userDepartmentRepository.findActiveDepartmentsByUserId(userId);
    }
    
    public List<Project> getUserProjects(Long userId) {
        return userProjectRepository.findActiveProjectsByUserId(userId);
    }
}
```

#### Required Repository Methods

```java
// UserDepartmentRepository
public interface UserDepartmentRepository extends JpaRepository<UserDepartment, Long> {
    boolean existsByUserIdAndDepartmentIdAndIsActive(Long userId, Long departmentId, boolean isActive);
    
    @Query("SELECT ud.department FROM UserDepartment ud WHERE ud.user.id = :userId AND ud.isActive = true")
    List<Department> findActiveDepartmentsByUserId(@Param("userId") Long userId);
}

// UserProjectRepository  
public interface UserProjectRepository extends JpaRepository<UserProject, Long> {
    boolean existsByUserIdAndProjectIdAndIsActive(Long userId, Long projectId, boolean isActive);
    
    @Query("SELECT up.project FROM UserProject up WHERE up.user.id = :userId AND up.isActive = true")
    List<Project> findActiveProjectsByUserId(@Param("userId") Long userId);
}
```

#### File Upload Controller Update

```java
@PostMapping("/upload")
public ResponseEntity<?> uploadFile(
    @RequestParam("file") MultipartFile file,
    @RequestParam(required = false) Long departmentId,
    @RequestParam(required = false) Long projectId,
    Authentication authentication) {
    
    User currentUser = getCurrentUser(authentication);
    
    // Validate upload permissions
    if (departmentId != null) {
        if (!fileUploadValidationService.canUploadToDepartment(
            currentUser.getId(), departmentId, currentUser.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("You don't have permission to upload files to this department");
        }
    }
    
    if (projectId != null) {
        if (!fileUploadValidationService.canUploadToProject(
            currentUser.getId(), projectId, currentUser.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("You don't have permission to upload files to this project");
        }
    }
    
    // Proceed with file upload...
    return fileService.uploadFile(file, departmentId, projectId, currentUser);
}
```

## Migration Steps

1. **Run the migration script**: `migration_user_department_project.sql`
2. **Update Entity Classes**: Add UserDepartment and UserProject entities
3. **Create Repositories**: Add repositories for the new entities
4. **Update File Upload Logic**: Implement validation service
5. **Update Frontend**: Modify UI to show user's departments and projects
6. **Test thoroughly**: Verify permissions work correctly

## Benefits of New Structure

1. **Flexibility**: Users can work across multiple departments
2. **Project Collaboration**: Cross-departmental project teams
3. **Better Access Control**: Granular permissions based on assignments
4. **Audit Trail**: Track who assigned users to departments/projects
5. **Role-based Access**: Different roles within departments and projects

## Sample Data

The updated schema includes sample data showing:
- Admin user assigned to multiple departments as manager
- Regular users assigned to specific departments
- Cross-department collaboration on projects
- Different roles (MEMBER, MANAGER, LEAD, COLLABORATOR)

This structure provides a solid foundation for enterprise-level file management with proper access controls and organizational flexibility.
