package com.duongdat.filehub.controller;

import com.duongdat.filehub.dto.response.ApiResponse;
import com.duongdat.filehub.dto.response.PageResponse;
import com.duongdat.filehub.entity.Department;
import com.duongdat.filehub.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {
    
    private final DepartmentService departmentService;
    
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Department>>> getAllActiveDepartments() {
        try {
            List<Department> departments = departmentService.getAllActiveDepartments();
            return ResponseEntity.ok(ApiResponse.success("Departments retrieved successfully", departments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<Department>>> getDepartmentsWithFilters(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long managerId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        try {
            PageResponse<Department> departments = departmentService.getDepartmentsWithFilters(
                name, managerId, isActive, page, size, sortBy, sortDirection);
            return ResponseEntity.ok(ApiResponse.success("Departments retrieved successfully", departments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Department>> getDepartmentById(@PathVariable Long id) {
        try {
            Department department = departmentService.getDepartmentById(id)
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            return ResponseEntity.ok(ApiResponse.success("Department retrieved successfully", department));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/root")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Department>>> getRootDepartments() {
        try {
            List<Department> departments = departmentService.getRootDepartments();
            return ResponseEntity.ok(ApiResponse.success("Root departments retrieved successfully", departments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}/subdepartments")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Department>>> getSubDepartments(@PathVariable Long id) {
        try {
            List<Department> subDepartments = departmentService.getSubDepartments(id);
            return ResponseEntity.ok(ApiResponse.success("Sub-departments retrieved successfully", subDepartments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Department>> createDepartment(@RequestBody Department department) {
        try {
            Department createdDepartment = departmentService.createDepartment(department);
            return ResponseEntity.ok(ApiResponse.success("Department created successfully", createdDepartment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Department>> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        try {
            Department updatedDepartment = departmentService.updateDepartment(id, department);
            return ResponseEntity.ok(ApiResponse.success("Department updated successfully", updatedDepartment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable Long id) {
        try {
            boolean deleted = departmentService.deleteDepartment(id);
            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success("Department deleted successfully", null));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("Department not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getDepartmentStats(@PathVariable Long id) {
        try {
            Long userCount = departmentService.getUserCountByDepartment(id);
            Long projectCount = departmentService.getActiveProjectCountByDepartment(id);
            
            var stats = java.util.Map.of(
                "userCount", userCount,
                "projectCount", projectCount
            );
            
            return ResponseEntity.ok(ApiResponse.success("Department statistics retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Department>>> getCurrentUserDepartments() {
        try {
            List<Department> departments = departmentService.getCurrentUserDepartments();
            return ResponseEntity.ok(ApiResponse.success("User departments retrieved successfully", departments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
