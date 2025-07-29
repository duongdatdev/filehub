package com.duongdat.filehub.controller;

import com.duongdat.filehub.dto.response.ApiResponse;
import com.duongdat.filehub.dto.response.PageResponse;
import com.duongdat.filehub.entity.Project;
import com.duongdat.filehub.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    
    private final ProjectService projectService;
    
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Project>>> getAllActiveProjects() {
        try {
            List<Project> projects = projectService.getAllActiveProjects();
            return ResponseEntity.ok(ApiResponse.success("Projects retrieved successfully", projects));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<Project>>> getProjectsWithFilters(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        try {
            PageResponse<Project> projects = projectService.getProjectsWithFilters(
                name, departmentId, status, page, size, sortBy, sortDirection);
            return ResponseEntity.ok(ApiResponse.success("Projects retrieved successfully", projects));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Project>> getProjectById(@PathVariable Long id) {
        try {
            Project project = projectService.getProjectById(id)
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            return ResponseEntity.ok(ApiResponse.success("Project retrieved successfully", project));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/department/{departmentId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Project>>> getProjectsByDepartment(@PathVariable Long departmentId) {
        try {
            List<Project> projects = projectService.getProjectsByDepartment(departmentId);
            return ResponseEntity.ok(ApiResponse.success("Projects retrieved successfully", projects));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Project>>> getProjectsByStatus(@PathVariable String status) {
        try {
            List<Project> projects = projectService.getProjectsByStatus(status);
            return ResponseEntity.ok(ApiResponse.success("Projects retrieved successfully", projects));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Project>> createProject(@RequestBody Project project) {
        try {
            Project createdProject = projectService.createProject(project);
            return ResponseEntity.ok(ApiResponse.success("Project created successfully", createdProject));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Project>> updateProject(@PathVariable Long id, @RequestBody Project project) {
        try {
            Project updatedProject = projectService.updateProject(id, project);
            return ResponseEntity.ok(ApiResponse.success("Project updated successfully", updatedProject));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Project>> updateProjectStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Project updatedProject = projectService.updateProjectStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("Project status updated successfully", updatedProject));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable Long id) {
        try {
            boolean deleted = projectService.deleteProject(id);
            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success("Project deleted successfully", null));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("Project not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}/stats")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getProjectStats(@PathVariable Long id) {
        try {
            Long fileCount = projectService.getFileCountByProject(id);
            
            var stats = java.util.Map.of(
                "fileCount", fileCount
            );
            
            return ResponseEntity.ok(ApiResponse.success("Project statistics retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Project>>> getCurrentUserProjects() {
        try {
            List<Project> projects = projectService.getCurrentUserProjects();
            return ResponseEntity.ok(ApiResponse.success("User projects retrieved successfully", projects));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
