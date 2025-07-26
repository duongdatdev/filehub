package com.duongdat.filehub.service;

import com.duongdat.filehub.entity.UserDepartment;
import com.duongdat.filehub.entity.UserProject;
import com.duongdat.filehub.repository.UserDepartmentRepository;
import com.duongdat.filehub.repository.UserProjectRepository;
import com.duongdat.filehub.repository.DepartmentRepository;
import com.duongdat.filehub.repository.ProjectRepository;
import com.duongdat.filehub.repository.UserRepository;
import com.duongdat.filehub.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserAssignmentService {
    
    private final UserDepartmentRepository userDepartmentRepository;
    private final UserProjectRepository userProjectRepository;
    private final DepartmentRepository departmentRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final UserAuthorizationService userAuthorizationService;
    
    /**
     * Assign user to department
     */
    public UserDepartment assignUserToDepartment(Long userId, Long departmentId, String role) {
        // Validate that current user can manage this department
        if (!userAuthorizationService.canManageDepartmentUsers(departmentId)) {
            throw new RuntimeException("You don't have permission to manage users in this department");
        }
        
        // Validate that user and department exist
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        if (!departmentRepository.existsById(departmentId)) {
            throw new RuntimeException("Department not found");
        }
        
        // Check if assignment already exists
        if (userDepartmentRepository.existsByUserIdAndDepartmentIdAndIsActiveTrue(userId, departmentId)) {
            throw new RuntimeException("User is already assigned to this department");
        }
        
        // Create new assignment
        UserDepartment assignment = new UserDepartment();
        assignment.setUserId(userId);
        assignment.setDepartmentId(departmentId);
        assignment.setRole(role != null ? role : "MEMBER");
        assignment.setIsActive(true);
        assignment.setAssignedAt(LocalDateTime.now());
        assignment.setAssignedBy(securityUtil.getCurrentUserId().orElse(null));
        
        return userDepartmentRepository.save(assignment);
    }
    
    /**
     * Remove user from department
     */
    public void removeUserFromDepartment(Long userId, Long departmentId) {
        // Validate that current user can manage this department
        if (!userAuthorizationService.canManageDepartmentUsers(departmentId)) {
            throw new RuntimeException("You don't have permission to manage users in this department");
        }
        
        UserDepartment assignment = userDepartmentRepository.findByUserIdAndDepartmentId(userId, departmentId)
                .orElseThrow(() -> new RuntimeException("User assignment not found"));
        
        assignment.setIsActive(false);
        userDepartmentRepository.save(assignment);
    }
    
    /**
     * Assign user to project
     */
    public UserProject assignUserToProject(Long userId, Long projectId, String role) {
        // Validate that current user can manage this project
        if (!userAuthorizationService.canManageProjectUsers(projectId)) {
            throw new RuntimeException("You don't have permission to manage users in this project");
        }
        
        // Validate that user and project exist
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        if (!projectRepository.existsById(projectId)) {
            throw new RuntimeException("Project not found");
        }
        
        // Check if assignment already exists
        if (userProjectRepository.existsByUserIdAndProjectIdAndIsActiveTrue(userId, projectId)) {
            throw new RuntimeException("User is already assigned to this project");
        }
        
        // Create new assignment
        UserProject assignment = new UserProject();
        assignment.setUserId(userId);
        assignment.setProjectId(projectId);
        assignment.setRole(role != null ? role : "MEMBER");
        assignment.setIsActive(true);
        assignment.setAssignedAt(LocalDateTime.now());
        assignment.setAssignedBy(securityUtil.getCurrentUserId().orElse(null));
        
        return userProjectRepository.save(assignment);
    }
    
    /**
     * Remove user from project
     */
    public void removeUserFromProject(Long userId, Long projectId) {
        // Validate that current user can manage this project
        if (!userAuthorizationService.canManageProjectUsers(projectId)) {
            throw new RuntimeException("You don't have permission to manage users in this project");
        }
        
        UserProject assignment = userProjectRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new RuntimeException("User assignment not found"));
        
        assignment.setIsActive(false);
        userProjectRepository.save(assignment);
    }
    
    /**
     * Update user role in department
     */
    public UserDepartment updateUserDepartmentRole(Long userId, Long departmentId, String newRole) {
        if (!userAuthorizationService.canManageDepartmentUsers(departmentId)) {
            throw new RuntimeException("You don't have permission to manage users in this department");
        }
        
        UserDepartment assignment = userDepartmentRepository.findByUserIdAndDepartmentId(userId, departmentId)
                .orElseThrow(() -> new RuntimeException("User assignment not found"));
        
        assignment.setRole(newRole);
        return userDepartmentRepository.save(assignment);
    }
    
    /**
     * Update user role in project
     */
    public UserProject updateUserProjectRole(Long userId, Long projectId, String newRole) {
        if (!userAuthorizationService.canManageProjectUsers(projectId)) {
            throw new RuntimeException("You don't have permission to manage users in this project");
        }
        
        UserProject assignment = userProjectRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new RuntimeException("User assignment not found"));
        
        assignment.setRole(newRole);
        return userProjectRepository.save(assignment);
    }
    
    /**
     * Get all users in a department
     */
    public List<UserDepartment> getDepartmentUsers(Long departmentId) {
        if (!userAuthorizationService.canViewDepartmentFiles(departmentId)) {
            throw new RuntimeException("You don't have permission to view users in this department");
        }
        
        return userDepartmentRepository.findByDepartmentIdAndIsActiveTrue(departmentId);
    }
    
    /**
     * Get all users in a project
     */
    public List<UserProject> getProjectUsers(Long projectId) {
        if (!userAuthorizationService.canViewProjectFiles(projectId)) {
            throw new RuntimeException("You don't have permission to view users in this project");
        }
        
        return userProjectRepository.findByProjectIdAndIsActiveTrue(projectId);
    }
    
    /**
     * Get user's department assignments
     */
    public List<UserDepartment> getUserDepartments(Long userId) {
        // Users can view their own assignments, admins can view any
        if (!userAuthorizationService.isCurrentUserAdmin() && 
            !securityUtil.getCurrentUserId().map(currentUserId -> currentUserId.equals(userId)).orElse(false)) {
            throw new RuntimeException("You don't have permission to view this user's assignments");
        }
        
        return userDepartmentRepository.findByUserIdAndIsActiveTrue(userId);
    }
    
    /**
     * Get user's project assignments
     */
    public List<UserProject> getUserProjects(Long userId) {
        // Users can view their own assignments, admins can view any
        if (!userAuthorizationService.isCurrentUserAdmin() && 
            !securityUtil.getCurrentUserId().map(currentUserId -> currentUserId.equals(userId)).orElse(false)) {
            throw new RuntimeException("You don't have permission to view this user's assignments");
        }
        
        return userProjectRepository.findByUserIdAndIsActiveTrue(userId);
    }
}
