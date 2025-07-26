package com.duongdat.filehub.service;

import com.duongdat.filehub.entity.Role;
import com.duongdat.filehub.entity.UserDepartment;
import com.duongdat.filehub.entity.UserProject;
import com.duongdat.filehub.repository.UserDepartmentRepository;
import com.duongdat.filehub.repository.UserProjectRepository;
import com.duongdat.filehub.repository.UserRepository;
import com.duongdat.filehub.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAuthorizationService {
    
    private final UserRepository userRepository;
    private final UserDepartmentRepository userDepartmentRepository;
    private final UserProjectRepository userProjectRepository;
    private final SecurityUtil securityUtil;
    
    /**
     * Check if current user is admin
     */
    public boolean isCurrentUserAdmin() {
        return securityUtil.getCurrentUserId()
                .map(this::isUserAdmin)
                .orElse(false);
    }
    
    /**
     * Check if user is admin
     */
    public boolean isUserAdmin(Long userId) {
        return userRepository.findById(userId)
                .map(user -> Role.ADMIN.equals(user.getRole()))
                .orElse(false);
    }
    
    /**
     * Check if current user can upload files to a specific department
     */
    public boolean canUploadToDepartment(Long departmentId) {
        // Admin can upload anywhere
        if (isCurrentUserAdmin()) {
            return true;
        }
        
        // Regular users must be assigned to the department
        return securityUtil.getCurrentUserId()
                .map(userId -> userDepartmentRepository.existsByUserIdAndDepartmentIdAndIsActiveTrue(userId, departmentId))
                .orElse(false);
    }
    
    /**
     * Check if current user can upload files to a specific project
     */
    public boolean canUploadToProject(Long projectId) {
        // Admin can upload anywhere
        if (isCurrentUserAdmin()) {
            return true;
        }
        
        // Regular users must be assigned to the project
        return securityUtil.getCurrentUserId()
                .map(userId -> userProjectRepository.existsByUserIdAndProjectIdAndIsActiveTrue(userId, projectId))
                .orElse(false);
    }
    
    /**
     * Check if current user can view files in a specific department
     */
    public boolean canViewDepartmentFiles(Long departmentId) {
        // Admin can view all files
        if (isCurrentUserAdmin()) {
            return true;
        }
        
        // Regular users can only view files in departments they're assigned to
        return securityUtil.getCurrentUserId()
                .map(userId -> userDepartmentRepository.existsByUserIdAndDepartmentIdAndIsActiveTrue(userId, departmentId))
                .orElse(false);
    }
    
    /**
     * Check if current user can view files in a specific project
     */
    public boolean canViewProjectFiles(Long projectId) {
        // Admin can view all files
        if (isCurrentUserAdmin()) {
            return true;
        }
        
        // Regular users can only view files in projects they're assigned to
        return securityUtil.getCurrentUserId()
                .map(userId -> userProjectRepository.existsByUserIdAndProjectIdAndIsActiveTrue(userId, projectId))
                .orElse(false);
    }
    
    /**
     * Get all department IDs that current user can access
     */
    public List<Long> getAccessibleDepartmentIds() {
        return securityUtil.getCurrentUserId()
                .map(userId -> {
                    if (isUserAdmin(userId)) {
                        // Admin can access all departments - return empty list to indicate "all"
                        return List.<Long>of();
                    } else {
                        // Regular users can only access assigned departments
                        return userDepartmentRepository.findDepartmentIdsByUserId(userId);
                    }
                })
                .orElse(List.of(-1L)); // Return -1 if no user authenticated (prevents access to anything)
    }
    
    /**
     * Get all project IDs that current user can access
     */
    public List<Long> getAccessibleProjectIds() {
        return securityUtil.getCurrentUserId()
                .map(userId -> {
                    if (isUserAdmin(userId)) {
                        // Admin can access all projects - return empty list to indicate "all"
                        return List.<Long>of();
                    } else {
                        // Regular users can only access assigned projects
                        return userProjectRepository.findProjectIdsByUserId(userId);
                    }
                })
                .orElse(List.of(-1L)); // Return -1 if no user authenticated (prevents access to anything)
    }
    
    /**
     * Check if current user can manage users in a department (assign/remove users)
     */
    public boolean canManageDepartmentUsers(Long departmentId) {
        // Admin can manage all
        if (isCurrentUserAdmin()) {
            return true;
        }
        
        // Department managers can manage their department
        return securityUtil.getCurrentUserId()
                .map(userId -> userDepartmentRepository.isUserManagerOfDepartment(userId, departmentId))
                .orElse(false);
    }
    
    /**
     * Check if current user can manage users in a project (assign/remove users)
     */
    public boolean canManageProjectUsers(Long projectId) {
        // Admin can manage all
        if (isCurrentUserAdmin()) {
            return true;
        }
        
        // Project leads can manage their project
        return securityUtil.getCurrentUserId()
                .map(userId -> userProjectRepository.isUserLeadOfProject(userId, projectId))
                .orElse(false);
    }
    
    /**
     * Validate file upload permissions
     */
    public void validateFileUploadPermissions(Long departmentId, Long projectId) {
        if (!isCurrentUserAdmin()) {
            // Check department access
            if (departmentId != null && !canUploadToDepartment(departmentId)) {
                throw new RuntimeException("You don't have permission to upload files to this department");
            }
            
            // Check project access
            if (projectId != null && !canUploadToProject(projectId)) {
                throw new RuntimeException("You don't have permission to upload files to this project");
            }
            
            // Ensure user is assigned to at least one department if not admin
            List<Long> userDepartments = userDepartmentRepository.findDepartmentIdsByUserId(
                    securityUtil.getCurrentUserId().orElseThrow(() -> new RuntimeException("User not authenticated"))
            );
            
            if (userDepartments.isEmpty()) {
                throw new RuntimeException("You must be assigned to a department to upload files");
            }
        }
    }
    
    /**
     * Get user's department assignments
     */
    public List<UserDepartment> getUserDepartments(Long userId) {
        return userDepartmentRepository.findByUserIdAndIsActiveTrue(userId);
    }
    
    /**
     * Get user's project assignments
     */
    public List<UserProject> getUserProjects(Long userId) {
        return userProjectRepository.findByUserIdAndIsActiveTrue(userId);
    }
    
    /**
     * Get current user's department assignments
     */
    public List<UserDepartment> getCurrentUserDepartments() {
        return securityUtil.getCurrentUserId()
                .map(this::getUserDepartments)
                .orElse(List.of());
    }
    
    /**
     * Get current user's project assignments
     */
    public List<UserProject> getCurrentUserProjects() {
        return securityUtil.getCurrentUserId()
                .map(this::getUserProjects)
                .orElse(List.of());
    }
}
