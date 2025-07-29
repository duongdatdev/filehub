package com.duongdat.filehub.service;

import com.duongdat.filehub.dto.response.PageResponse;
import com.duongdat.filehub.entity.Project;
import com.duongdat.filehub.repository.ProjectRepository;
import com.duongdat.filehub.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {
    
    private final ProjectRepository projectRepository;
    private final SecurityUtil securityUtil;
    
    public List<Project> getAllActiveProjects() {
        return projectRepository.findAllByOrderByCreatedAtDesc();
    }
    
    public PageResponse<Project> getProjectsWithFilters(String name, Long departmentId, 
                                                      String status, int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Project> projectPage = projectRepository.findProjectsWithFilters(
            name, departmentId, status, pageable);
        
        return new PageResponse<Project>(
            projectPage.getContent(),
            projectPage.getNumber(),
            projectPage.getSize(),
            projectPage.getTotalElements(),
            projectPage.getTotalPages(),
            projectPage.isFirst(),
            projectPage.isLast(),
            projectPage.hasNext(),
            projectPage.hasPrevious()
        );
    }
    
    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }
    
    public List<Project> getProjectsByDepartment(Long departmentId) {
        return projectRepository.findByDepartmentIdOrderByCreatedAtDesc(departmentId);
    }
    
    public List<Project> getProjectsByStatus(String status) {
        return projectRepository.findByStatusOrderByCreatedAtDesc(status);
    }
    
    public Project createProject(Project project) {
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        return projectRepository.save(project);
    }
    
    public Project updateProject(Long id, Project projectDetails) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        
        project.setName(projectDetails.getName());
        project.setDescription(projectDetails.getDescription());
        project.setDepartmentId(projectDetails.getDepartmentId());
        project.setStatus(projectDetails.getStatus());
        project.setUpdatedAt(LocalDateTime.now());
        
        return projectRepository.save(project);
    }
    
    public boolean deleteProject(Long id) {
        Optional<Project> projectOpt = projectRepository.findById(id);
        if (projectOpt.isPresent()) {
            // Check if project has files
            Long fileCount = projectRepository.countFilesByProjectId(id);
            if (fileCount > 0) {
                throw new RuntimeException("Cannot delete project with associated files");
            }
            
            // Hard delete since we don't have isActive field
            projectRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public Long getFileCountByProject(Long projectId) {
        return projectRepository.countFilesByProjectId(projectId);
    }
    
    public Project updateProjectStatus(Long id, String status) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        
        project.setStatus(status);
        project.setUpdatedAt(LocalDateTime.now());
        
        return projectRepository.save(project);
    }
    
    public List<Project> getCurrentUserProjects() {
        Long currentUserId = securityUtil.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));
        
        return projectRepository.findProjectsByUserId(currentUserId);
    }
}
