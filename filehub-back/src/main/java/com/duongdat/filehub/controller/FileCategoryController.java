package com.duongdat.filehub.controller;

import com.duongdat.filehub.dto.response.ApiResponse;
import com.duongdat.filehub.entity.FileCategory;
import com.duongdat.filehub.service.FileCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class FileCategoryController {
    
    private final FileCategoryService fileCategoryService;
    
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<FileCategory>>> getAllCategories() {
        try {
            List<FileCategory> categories = fileCategoryService.getAllActiveCategories();
            return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", categories));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/root")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<FileCategory>>> getRootCategories() {
        try {
            List<FileCategory> categories = fileCategoryService.getRootCategories();
            return ResponseEntity.ok(ApiResponse.success("Root categories retrieved successfully", categories));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FileCategory>> getCategoryById(@PathVariable Long id) {
        try {
            FileCategory category = fileCategoryService.getCategoryById(id)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            return ResponseEntity.ok(ApiResponse.success("Category retrieved successfully", category));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}/subcategories")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<FileCategory>>> getSubCategories(@PathVariable Long id) {
        try {
            List<FileCategory> subCategories = fileCategoryService.getSubCategories(id);
            return ResponseEntity.ok(ApiResponse.success("Subcategories retrieved successfully", subCategories));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
