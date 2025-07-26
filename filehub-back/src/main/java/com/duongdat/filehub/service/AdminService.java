package com.duongdat.filehub.service;

import com.duongdat.filehub.dto.request.AdminUserFilterRequest;
import com.duongdat.filehub.dto.request.UserAssignmentRequest;
import com.duongdat.filehub.dto.response.DashboardStatsResponse;
import com.duongdat.filehub.dto.response.PageResponse;
import com.duongdat.filehub.dto.response.RecentActivityResponse;
import com.duongdat.filehub.dto.response.UserResponse;
import com.duongdat.filehub.entity.User;
import com.duongdat.filehub.repository.UserRepository;
import com.duongdat.filehub.repository.DepartmentRepository;
import com.duongdat.filehub.repository.ProjectRepository;
import com.duongdat.filehub.repository.FileRepository;
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

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final ProjectRepository projectRepository;
    private final FileRepository fileRepository;

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
        
        // In a real implementation, you would have user-department relationships
        // For now, just return the user response
        return mapToUserResponse(user);
    }

    public UserResponse assignUserToProject(Long userId, Long projectId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // In a real implementation, you would have user-project relationships
        // For now, just return the user response
        return mapToUserResponse(user);
    }

    public UserResponse removeUserFromProject(Long userId, Long projectId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // In a real implementation, you would remove user-project relationships
        // For now, just return the user response
        return mapToUserResponse(user);
    }

    public List<UserResponse> bulkAssignUsers(List<UserAssignmentRequest> assignments) {
        List<UserResponse> result = new ArrayList<>();
        
        for (UserAssignmentRequest assignment : assignments) {
            User user = userRepository.findById(assignment.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + assignment.getUserId()));
            
            // In a real implementation, you would perform the actual assignments
            // For now, just add to result
            result.add(mapToUserResponse(user));
        }
        
        return result;
    }
}
