package com.duongdat.filehub.service;

import com.duongdat.filehub.dto.request.AdminUserFilterRequest;
import com.duongdat.filehub.dto.request.UserAssignmentRequest;
import com.duongdat.filehub.dto.request.BatchUserAssignmentRequest;
import com.duongdat.filehub.dto.response.DashboardStatsResponse;
import com.duongdat.filehub.dto.response.PageResponse;
import com.duongdat.filehub.dto.response.RecentActivityResponse;
import com.duongdat.filehub.dto.response.UserResponse;
import com.duongdat.filehub.dto.response.AdminUserDetailResponse;
import com.duongdat.filehub.dto.response.UserDepartmentResponse;
import com.duongdat.filehub.dto.response.UserProjectResponse;
import com.duongdat.filehub.dto.response.UserDepartmentSummary;
import com.duongdat.filehub.dto.response.UserProjectSummary;
import com.duongdat.filehub.entity.User;
import com.duongdat.filehub.entity.UserDepartment;
import com.duongdat.filehub.entity.UserProject;
import com.duongdat.filehub.entity.Department;
import com.duongdat.filehub.entity.Project;
import com.duongdat.filehub.repository.UserRepository;
import com.duongdat.filehub.repository.DepartmentRepository;
import com.duongdat.filehub.repository.ProjectRepository;
import com.duongdat.filehub.repository.FileRepository;
import com.duongdat.filehub.service.UserAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final ProjectRepository projectRepository;
    private final FileRepository fileRepository;
    private final UserAssignmentService userAssignmentService;

    public PageResponse<UserResponse> getAllUsers(AdminUserFilterRequest request) {
        // Create sort
        Sort.Direction direction = request.getSortDirection().equalsIgnoreCase("DESC") 
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, request.getSortBy());
        
        // Create pageable
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        
        // Get users with filters
        Page<User> userPage = userRepository.findUsersWithFilters(
            request.getUsername(),
            request.getEmail(),
            request.getRole(),
            request.getIsActive(),
            pageable
        );
        
        // Convert to UserResponse DTOs
        List<UserResponse> userResponses = userPage.getContent().stream()
            .map(this::mapToUserResponse)
            .toList();
        
        // Create PageResponse
        return new PageResponse<>(
            userResponses,
            userPage.getNumber(),
            userPage.getSize(),
            userPage.getTotalElements(),
            userPage.getTotalPages(),
            userPage.isFirst(),
            userPage.isLast(),
            userPage.hasNext(),
            userPage.hasPrevious()
        );
    }

    public Optional<UserResponse> getUserById(Long id) {
        return userRepository.findById(id)
            .map(this::mapToUserResponse);
    }

    public UserResponse updateUserStatus(Long id, boolean isActive) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setActive(isActive);
        User savedUser = userRepository.save(user);
        
        return mapToUserResponse(savedUser);
    }

    public UserResponse updateUserRole(Long id, com.duongdat.filehub.entity.Role role) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setRole(role);
        User savedUser = userRepository.save(user);
        
        return mapToUserResponse(savedUser);
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFullName() != null ? user.getFullName() : user.getUsername(),
            user.getRole().toString(),
            user.isActive(),
            user.getCreatedAt()
        );
    }

    // Dashboard methods
    public DashboardStatsResponse getDashboardStats() {
        Long totalUsers = userRepository.count();
        Long totalDepartments = departmentRepository.count();
        Long activeProjects = projectRepository.count();
        Long totalFiles = fileRepository.count();
        
        return new DashboardStatsResponse(totalUsers, totalDepartments, activeProjects, totalFiles);
    }

    public List<RecentActivityResponse> getRecentActivity() {
        // For now, return mock data. In a real implementation, 
        // you would have an audit log table to track activities
        List<RecentActivityResponse> activities = new ArrayList<>();
        
        activities.add(new RecentActivityResponse(
            1L, "user", "New user registered", "System", LocalDateTime.now().minusMinutes(30)
        ));
        activities.add(new RecentActivityResponse(
            2L, "project", "Project status updated", "Admin", LocalDateTime.now().minusHours(2)
        ));
        activities.add(new RecentActivityResponse(
            3L, "department", "Department manager changed", "Admin", LocalDateTime.now().minusHours(4)
        ));
        
        return activities;
    }

    // User assignment methods
    public UserResponse assignUserToDepartment(Long userId, Long departmentId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Use UserAssignmentService to perform the actual assignment
        userAssignmentService.assignUserToDepartment(userId, departmentId, "MEMBER");
        
        return mapToUserResponse(user);
    }

    public UserResponse assignUserToProject(Long userId, Long projectId) {
        return assignUserToProject(userId, projectId, "MEMBER");
    }

    public UserResponse assignUserToProject(Long userId, Long projectId, String role) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Use UserAssignmentService to perform the actual assignment
        userAssignmentService.assignUserToProject(userId, projectId, role != null ? role : "MEMBER");
        
        return mapToUserResponse(user);
    }

    public UserResponse removeUserFromProject(Long userId, Long projectId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Use UserAssignmentService to remove the assignment
        userAssignmentService.removeUserFromProject(userId, projectId);
        
        return mapToUserResponse(user);
    }

    public UserResponse removeUserFromDepartment(Long userId, Long departmentId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Use UserAssignmentService to remove the assignment
        userAssignmentService.removeUserFromDepartment(userId, departmentId);
        
        return mapToUserResponse(user);
    }

    public UserResponse updateUserDepartmentRole(Long userId, Long departmentId, String role) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Use UserAssignmentService to update the role
        userAssignmentService.updateUserDepartmentRole(userId, departmentId, role);
        
        return mapToUserResponse(user);
    }

    public UserResponse updateUserProjectRole(Long userId, Long projectId, String role) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Use UserAssignmentService to update the role
        userAssignmentService.updateUserProjectRole(userId, projectId, role);
        
        return mapToUserResponse(user);
    }

    public List<UserDepartment> getUserDepartments(Long userId) {
        // Validate user exists
        userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return userAssignmentService.getUserDepartments(userId);
    }

    public List<UserProject> getUserProjects(Long userId) {
        // Validate user exists
        userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return userAssignmentService.getUserProjects(userId);
    }

    public AdminUserDetailResponse getUserDetailWithAssignments(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserResponse userResponse = mapToUserResponse(user);
        List<UserDepartment> departmentEntities = userAssignmentService.getUserDepartments(userId);
        List<UserProject> projectEntities = userAssignmentService.getUserProjects(userId);
        
        // Convert entities to summary DTOs to avoid serialization issues
        List<UserDepartmentSummary> departments = departmentEntities.stream()
                .map(this::mapToUserDepartmentSummary)
                .collect(Collectors.toList());
        
        List<UserProjectSummary> projects = projectEntities.stream()
                .map(this::mapToUserProjectSummary)
                .collect(Collectors.toList());
        
        return new AdminUserDetailResponse(userResponse, departments, projects);
    }

    public List<UserResponse> batchUpdateUserAssignments(BatchUserAssignmentRequest request) {
        List<UserResponse> result = new ArrayList<>();
        
        for (Long userId : request.getUserIds()) {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
            
            try {
                switch (request.getOperation().toUpperCase()) {
                    case "ADD":
                        if (request.getDepartmentId() != null) {
                            userAssignmentService.assignUserToDepartment(
                                userId, 
                                request.getDepartmentId(), 
                                request.getRole() != null ? request.getRole() : "MEMBER"
                            );
                        }
                        if (request.getProjectId() != null) {
                            userAssignmentService.assignUserToProject(
                                userId, 
                                request.getProjectId(), 
                                request.getRole() != null ? request.getRole() : "MEMBER"
                            );
                        }
                        break;
                        
                    case "REMOVE":
                        if (request.getDepartmentId() != null) {
                            userAssignmentService.removeUserFromDepartment(userId, request.getDepartmentId());
                        }
                        if (request.getProjectId() != null) {
                            userAssignmentService.removeUserFromProject(userId, request.getProjectId());
                        }
                        break;
                        
                    case "UPDATE_ROLE":
                        if (request.getDepartmentId() != null && request.getRole() != null) {
                            userAssignmentService.updateUserDepartmentRole(userId, request.getDepartmentId(), request.getRole());
                        }
                        if (request.getProjectId() != null && request.getRole() != null) {
                            userAssignmentService.updateUserProjectRole(userId, request.getProjectId(), request.getRole());
                        }
                        break;
                        
                    default:
                        throw new RuntimeException("Invalid operation: " + request.getOperation());
                }
                
                result.add(mapToUserResponse(user));
            } catch (Exception e) {
                // Log error but continue with other users
                System.err.println("Error processing user " + userId + ": " + e.getMessage());
            }
        }
        
        return result;
    }

    public List<UserResponse> bulkAssignUsers(List<UserAssignmentRequest> assignments) {
        List<UserResponse> result = new ArrayList<>();
        
        for (UserAssignmentRequest assignment : assignments) {
            User user = userRepository.findById(assignment.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + assignment.getUserId()));
            
            // Perform actual assignments based on request
            if (assignment.getDepartmentId() != null) {
                userAssignmentService.assignUserToDepartment(
                    assignment.getUserId(), 
                    assignment.getDepartmentId(), 
                    assignment.getRole() != null ? assignment.getRole() : "MEMBER"
                );
            }
            
            if (assignment.getProjectId() != null) {
                userAssignmentService.assignUserToProject(
                    assignment.getUserId(), 
                    assignment.getProjectId(), 
                    assignment.getRole() != null ? assignment.getRole() : "MEMBER"
                );
            }
            
            if (assignment.getProjectIds() != null && !assignment.getProjectIds().isEmpty()) {
                for (Long projectId : assignment.getProjectIds()) {
                    userAssignmentService.assignUserToProject(
                        assignment.getUserId(), 
                        projectId, 
                        assignment.getRole() != null ? assignment.getRole() : "MEMBER"
                    );
                }
            }
            
            result.add(mapToUserResponse(user));
        }
        
        return result;
    }
    
    // Mapping methods to convert entities to DTOs
    private UserDepartmentSummary mapToUserDepartmentSummary(UserDepartment userDepartment) {
        UserDepartmentSummary summary = new UserDepartmentSummary();
        summary.setId(userDepartment.getDepartmentId()); // Use departmentId as id for frontend compatibility
        summary.setRole(userDepartment.getRole());
        
        // Safely get department name
        try {
            Department department = departmentRepository.findById(userDepartment.getDepartmentId()).orElse(null);
            if (department != null) {
                summary.setName(department.getName());
            }
        } catch (Exception e) {
            // Ignore any lazy loading issues
            summary.setName("Unknown Department");
        }
        
        return summary;
    }
    
    private UserProjectSummary mapToUserProjectSummary(UserProject userProject) {
        UserProjectSummary summary = new UserProjectSummary();
        summary.setId(userProject.getProjectId()); // Use projectId as id for frontend compatibility
        summary.setRole(userProject.getRole());
        
        // Safely get project name
        try {
            Project project = projectRepository.findById(userProject.getProjectId()).orElse(null);
            if (project != null) {
                summary.setName(project.getName());
            }
        } catch (Exception e) {
            // Ignore any lazy loading issues
            summary.setName("Unknown Project");
        }
        
        return summary;
    }
    
    private UserDepartmentResponse mapToUserDepartmentResponse(UserDepartment userDepartment) {
        UserDepartmentResponse response = new UserDepartmentResponse();
        response.setId(userDepartment.getId());
        response.setUserId(userDepartment.getUserId());
        response.setDepartmentId(userDepartment.getDepartmentId());
        response.setRole(userDepartment.getRole());
        response.setIsActive(userDepartment.getIsActive());
        response.setAssignedAt(userDepartment.getAssignedAt());
        response.setAssignedBy(userDepartment.getAssignedBy());
        
        // Safely get department name
        try {
            Department department = departmentRepository.findById(userDepartment.getDepartmentId()).orElse(null);
            if (department != null) {
                response.setDepartmentName(department.getName());
            }
        } catch (Exception e) {
            // Ignore any lazy loading issues
        }
        
        // Safely get assigned by user name
        try {
            if (userDepartment.getAssignedBy() != null) {
                User assignedByUser = userRepository.findById(userDepartment.getAssignedBy()).orElse(null);
                if (assignedByUser != null) {
                    response.setAssignedByName(assignedByUser.getFullName());
                }
            }
        } catch (Exception e) {
            // Ignore any lazy loading issues
        }
        
        return response;
    }
    
    private UserProjectResponse mapToUserProjectResponse(UserProject userProject) {
        UserProjectResponse response = new UserProjectResponse();
        response.setId(userProject.getId());
        response.setUserId(userProject.getUserId());
        response.setProjectId(userProject.getProjectId());
        response.setRole(userProject.getRole());
        response.setIsActive(userProject.getIsActive());
        response.setAssignedAt(userProject.getAssignedAt());
        response.setAssignedBy(userProject.getAssignedBy());
        
        // Safely get project name
        try {
            Project project = projectRepository.findById(userProject.getProjectId()).orElse(null);
            if (project != null) {
                response.setProjectName(project.getName());
            }
        } catch (Exception e) {
            // Ignore any lazy loading issues
        }
        
        // Safely get assigned by user name
        try {
            if (userProject.getAssignedBy() != null) {
                User assignedByUser = userRepository.findById(userProject.getAssignedBy()).orElse(null);
                if (assignedByUser != null) {
                    response.setAssignedByName(assignedByUser.getFullName());
                }
            }
        } catch (Exception e) {
            // Ignore any lazy loading issues
        }
        
        return response;
    }
}
