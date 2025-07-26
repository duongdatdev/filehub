package com.duongdat.filehub.controller;

import com.duongdat.filehub.dto.request.UserAssignmentRequest;
import com.duongdat.filehub.dto.response.ApiResponse;
import com.duongdat.filehub.entity.UserDepartment;
import com.duongdat.filehub.entity.UserProject;
import com.duongdat.filehub.service.UserAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-assignments")
@RequiredArgsConstructor
public class UserAssignmentController {
    
    private final UserAssignmentService userAssignmentService;
    
    // Department Assignment Endpoints
    
    @PostMapping("/departments")
    public ResponseEntity<ApiResponse<UserDepartment>> assignUserToDepartment(
            @RequestBody UserAssignmentRequest request) {
        try {
            UserDepartment assignment = userAssignmentService.assignUserToDepartment(
                    request.getUserId(), 
                    request.getDepartmentId(), 
                    request.getRole()
            );
            return ResponseEntity.ok(ApiResponse.success("User successfully assigned to department", assignment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/departments/{userId}/{departmentId}")
    public ResponseEntity<ApiResponse<String>> removeUserFromDepartment(
            @PathVariable Long userId, 
            @PathVariable Long departmentId) {
        try {
            userAssignmentService.removeUserFromDepartment(userId, departmentId);
            return ResponseEntity.ok(ApiResponse.success("User successfully removed from department"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/departments/{userId}/{departmentId}/role")
    public ResponseEntity<ApiResponse<UserDepartment>> updateUserDepartmentRole(
            @PathVariable Long userId, 
            @PathVariable Long departmentId,
            @RequestParam String role) {
        try {
            UserDepartment assignment = userAssignmentService.updateUserDepartmentRole(userId, departmentId, role);
            return ResponseEntity.ok(ApiResponse.success("User role updated successfully", assignment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/departments/{departmentId}/users")
    public ResponseEntity<ApiResponse<List<UserDepartment>>> getDepartmentUsers(
            @PathVariable Long departmentId) {
        try {
            List<UserDepartment> users = userAssignmentService.getDepartmentUsers(departmentId);
            return ResponseEntity.ok(ApiResponse.success("Department users retrieved successfully", users));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // Project Assignment Endpoints
    
    @PostMapping("/projects")
    public ResponseEntity<ApiResponse<UserProject>> assignUserToProject(
            @RequestBody UserAssignmentRequest request) {
        try {
            UserProject assignment = userAssignmentService.assignUserToProject(
                    request.getUserId(), 
                    request.getProjectId(), 
                    request.getRole()
            );
            return ResponseEntity.ok(ApiResponse.success("User successfully assigned to project", assignment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/projects/{userId}/{projectId}")
    public ResponseEntity<ApiResponse<String>> removeUserFromProject(
            @PathVariable Long userId, 
            @PathVariable Long projectId) {
        try {
            userAssignmentService.removeUserFromProject(userId, projectId);
            return ResponseEntity.ok(ApiResponse.success("User successfully removed from project"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/projects/{userId}/{projectId}/role")
    public ResponseEntity<ApiResponse<UserProject>> updateUserProjectRole(
            @PathVariable Long userId, 
            @PathVariable Long projectId,
            @RequestParam String role) {
        try {
            UserProject assignment = userAssignmentService.updateUserProjectRole(userId, projectId, role);
            return ResponseEntity.ok(ApiResponse.success("User role updated successfully", assignment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/projects/{projectId}/users")
    public ResponseEntity<ApiResponse<List<UserProject>>> getProjectUsers(
            @PathVariable Long projectId) {
        try {
            List<UserProject> users = userAssignmentService.getProjectUsers(projectId);
            return ResponseEntity.ok(ApiResponse.success("Project users retrieved successfully", users));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // User Assignment Info Endpoints
    
    @GetMapping("/users/{userId}/departments")
    public ResponseEntity<ApiResponse<List<UserDepartment>>> getUserDepartments(
            @PathVariable Long userId) {
        try {
            List<UserDepartment> departments = userAssignmentService.getUserDepartments(userId);
            return ResponseEntity.ok(ApiResponse.success("User departments retrieved successfully", departments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/users/{userId}/projects")
    public ResponseEntity<ApiResponse<List<UserProject>>> getUserProjects(
            @PathVariable Long userId) {
        try {
            List<UserProject> projects = userAssignmentService.getUserProjects(userId);
            return ResponseEntity.ok(ApiResponse.success("User projects retrieved successfully", projects));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
