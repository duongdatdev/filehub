package com.duongdat.filehub.controller;

import com.duongdat.filehub.dto.request.AdminUserFilterRequest;
import com.duongdat.filehub.dto.request.UpdateUserStatusRequest;
import com.duongdat.filehub.dto.response.ApiResponse;
import com.duongdat.filehub.dto.response.PageResponse;
import com.duongdat.filehub.dto.response.UserResponse;
import com.duongdat.filehub.entity.Role;
import com.duongdat.filehub.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
}
