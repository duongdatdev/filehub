package com.duongdat.filehub.controller;

import com.duongdat.filehub.dto.request.AdminUserFilterRequest;
import com.duongdat.filehub.dto.request.UpdateUserStatusRequest;
import com.duongdat.filehub.dto.request.UserAssignmentRequest;
import com.duongdat.filehub.dto.request.BatchUserAssignmentRequest;
import com.duongdat.filehub.dto.response.ApiResponse;
import com.duongdat.filehub.dto.response.DashboardStatsResponse;
import com.duongdat.filehub.dto.response.PageResponse;
import com.duongdat.filehub.dto.response.UserResponse;
import com.duongdat.filehub.dto.response.RecentActivityResponse;
import com.duongdat.filehub.dto.response.AdminUserDetailResponse;
import com.duongdat.filehub.entity.Role;
import com.duongdat.filehub.entity.UserDepartment;
import com.duongdat.filehub.entity.UserProject;
import com.duongdat.filehub.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        
        try {
            AdminUserFilterRequest request = new AdminUserFilterRequest(
                username, email, role, isActive, page, size, sortBy, sortDirection
            );
            
            PageResponse<UserResponse> users = adminService.getAllUsers(request);
            return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        try {
            UserResponse user = adminService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateUserStatusRequest request) {
        try {
            UserResponse user = adminService.updateUserStatus(id, request.getIsActive());
            return ResponseEntity.ok(ApiResponse.success("User status updated successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/users/{id}/details")
    public ResponseEntity<ApiResponse<AdminUserDetailResponse>> getUserDetailsWithAssignments(@PathVariable Long id) {
        try {
            AdminUserDetailResponse userDetail = adminService.getUserDetailWithAssignments(id);
            return ResponseEntity.ok(ApiResponse.success("User details with assignments retrieved successfully", userDetail));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // Dashboard endpoints
    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats() {
        try {
            DashboardStatsResponse stats = adminService.getDashboardStats();
            return ResponseEntity.ok(ApiResponse.success("Dashboard stats retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/dashboard/recent-activity")
    public ResponseEntity<ApiResponse<List<RecentActivityResponse>>> getRecentActivity() {
        try {
            List<RecentActivityResponse> activities = adminService.getRecentActivity();
            return ResponseEntity.ok(ApiResponse.success("Recent activity retrieved successfully", activities));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // User assignment endpoints
    @PostMapping("/users/{userId}/department")
    public ResponseEntity<ApiResponse<UserResponse>> assignUserToDepartment(
            @PathVariable Long userId,
            @RequestBody UserAssignmentRequest request) {
        try {
            UserResponse user = adminService.assignUserToDepartment(userId, request.getDepartmentId());
            return ResponseEntity.ok(ApiResponse.success("User assigned to department successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/users/{userId}/projects")
    public ResponseEntity<ApiResponse<UserResponse>> assignUserToProject(
            @PathVariable Long userId,
            @RequestBody UserAssignmentRequest request) {
        try {
            UserResponse user = adminService.assignUserToProject(userId, request.getProjectIds().get(0));
            return ResponseEntity.ok(ApiResponse.success("User assigned to project successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/users/{userId}/projects/{projectId}")
    public ResponseEntity<ApiResponse<UserResponse>> removeUserFromProject(
            @PathVariable Long userId,
            @PathVariable Long projectId) {
        try {
            UserResponse user = adminService.removeUserFromProject(userId, projectId);
            return ResponseEntity.ok(ApiResponse.success("User removed from project successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/users/{userId}/departments/{departmentId}")
    public ResponseEntity<ApiResponse<UserResponse>> removeUserFromDepartment(
            @PathVariable Long userId,
            @PathVariable Long departmentId) {
        try {
            UserResponse user = adminService.removeUserFromDepartment(userId, departmentId);
            return ResponseEntity.ok(ApiResponse.success("User removed from department successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/users/{userId}/departments/{departmentId}/role")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserDepartmentRole(
            @PathVariable Long userId,
            @PathVariable Long departmentId,
            @RequestParam String role) {
        try {
            UserResponse user = adminService.updateUserDepartmentRole(userId, departmentId, role);
            return ResponseEntity.ok(ApiResponse.success("User department role updated successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/users/{userId}/projects/{projectId}/role")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserProjectRole(
            @PathVariable Long userId,
            @PathVariable Long projectId,
            @RequestParam String role) {
        try {
            UserResponse user = adminService.updateUserProjectRole(userId, projectId, role);
            return ResponseEntity.ok(ApiResponse.success("User project role updated successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/users/{userId}/departments")
    public ResponseEntity<ApiResponse<List<UserDepartment>>> getUserDepartments(
            @PathVariable Long userId) {
        try {
            List<UserDepartment> departments = adminService.getUserDepartments(userId);
            return ResponseEntity.ok(ApiResponse.success("User departments retrieved successfully", departments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/users/{userId}/projects")
    public ResponseEntity<ApiResponse<List<UserProject>>> getUserProjects(
            @PathVariable Long userId) {
        try {
            List<UserProject> projects = adminService.getUserProjects(userId);
            return ResponseEntity.ok(ApiResponse.success("User projects retrieved successfully", projects));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/users/bulk-assign")
    public ResponseEntity<ApiResponse<List<UserResponse>>> bulkAssignUsers(
            @RequestBody List<UserAssignmentRequest> assignments) {
        try {
            List<UserResponse> users = adminService.bulkAssignUsers(assignments);
            return ResponseEntity.ok(ApiResponse.success("Bulk assignment completed successfully", users));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/users/batch-update")
    public ResponseEntity<ApiResponse<List<UserResponse>>> batchUpdateUserAssignments(
            @RequestBody BatchUserAssignmentRequest request) {
        try {
            List<UserResponse> users = adminService.batchUpdateUserAssignments(request);
            return ResponseEntity.ok(ApiResponse.success("Batch update completed successfully", users));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
