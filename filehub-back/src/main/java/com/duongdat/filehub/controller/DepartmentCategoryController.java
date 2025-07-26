package com.duongdat.filehub.controller;

import com.duongdat.filehub.dto.response.ApiResponse;
import com.duongdat.filehub.entity.DepartmentCategory;
import com.duongdat.filehub.service.DepartmentCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/department-categories")
@RequiredArgsConstructor
public class DepartmentCategoryController {
    
    private final DepartmentCategoryService departmentCategoryService;
    
    @GetMapping("/department/{departmentId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<DepartmentCategory>>> getCategoriesByDepartment(@PathVariable Long departmentId) {
        try {
            List<DepartmentCategory> categories = departmentCategoryService.getCategoriesByDepartment(departmentId);
            return ResponseEntity.ok(ApiResponse.success("Department categories retrieved successfully", categories));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/department/{departmentId}/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<DepartmentCategory>>> getAllCategoriesByDepartment(@PathVariable Long departmentId) {
        try {
            List<DepartmentCategory> categories = departmentCategoryService.getAllCategoriesByDepartment(departmentId);
            return ResponseEntity.ok(ApiResponse.success("All department categories retrieved successfully", categories));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DepartmentCategory>> getCategoryById(@PathVariable Long id) {
        try {
            DepartmentCategory category = departmentCategoryService.getCategoryById(id)
                    .orElseThrow(() -> new RuntimeException("Department category not found"));
            return ResponseEntity.ok(ApiResponse.success("Department category retrieved successfully", category));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DepartmentCategory>> createCategory(@RequestBody DepartmentCategory category) {
        try {
            DepartmentCategory createdCategory = departmentCategoryService.createCategory(category);
            return ResponseEntity.ok(ApiResponse.success("Department category created successfully", createdCategory));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DepartmentCategory>> updateCategory(@PathVariable Long id, @RequestBody DepartmentCategory category) {
        try {
            DepartmentCategory updatedCategory = departmentCategoryService.updateCategory(id, category);
            return ResponseEntity.ok(ApiResponse.success("Department category updated successfully", updatedCategory));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        try {
            departmentCategoryService.deleteCategory(id);
            return ResponseEntity.ok(ApiResponse.success("Department category deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateCategory(@PathVariable Long id) {
        try {
            departmentCategoryService.deactivateCategory(id);
            return ResponseEntity.ok(ApiResponse.success("Department category deactivated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/department/{departmentId}/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getCategoryCountByDepartment(@PathVariable Long departmentId) {
        try {
            Long count = departmentCategoryService.getActiveCategoryCountByDepartment(departmentId);
            return ResponseEntity.ok(ApiResponse.success("Category count retrieved successfully", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
