package com.duongdat.filehub.service;

import com.duongdat.filehub.dto.response.PageResponse;
import com.duongdat.filehub.entity.Project;
import com.duongdat.filehub.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {
    
    private final ProjectRepository projectRepository;
    
    public List<Project> getAllActiveProjects() {
        return projectRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }
    
    public PageResponse<Project> getProjectsWithFilters(String name, Long departmentId, Long managerId, 
                                                      String status, String priority, Boolean isActive,
                                                      int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Project> projectPage = projectRepository.findProjectsWithFilters(
            name, departmentId, managerId, status, priority, isActive, pageable);
        
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
        return projectRepository.findByDepartmentIdAndIsActiveTrueOrderByCreatedAtDesc(departmentId);
    }
    
    public List<Project> getProjectsByManager(Long managerId) {
        return projectRepository.findByManagerIdAndIsActiveTrueOrderByCreatedAtDesc(managerId);
    }
    
    public List<Project> getProjectsByStatus(String status) {
        return projectRepository.findByStatusAndIsActiveTrueOrderByCreatedAtDesc(status);
    }
    
    public List<Project> getProjectsByPriority(String priority) {
        return projectRepository.findByPriorityAndIsActiveTrueOrderByCreatedAtDesc(priority);
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
        project.setManagerId(projectDetails.getManagerId());
        project.setStartDate(projectDetails.getStartDate());
        project.setEndDate(projectDetails.getEndDate());
        project.setStatus(projectDetails.getStatus());
        project.setPriority(projectDetails.getPriority());
        project.setBudget(projectDetails.getBudget());
        project.setUpdatedAt(LocalDateTime.now());
        
        return projectRepository.save(project);
    }
    
    public boolean deleteProject(Long id) {
        Optional<Project> projectOpt = projectRepository.findById(id);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            
            // Check if project has files
            Long fileCount = projectRepository.countFilesByProjectId(id);
            if (fileCount > 0) {
                throw new RuntimeException("Cannot delete project with associated files");
            }
            
            // Soft delete
            project.setIsActive(false);
            project.setUpdatedAt(LocalDateTime.now());
            projectRepository.save(project);
            return true;
        }
        return false;
    }
    
    public Long getFileCountByProject(Long projectId) {
        return projectRepository.countFilesByProjectId(projectId);
    }
    
    public List<Project> getOverdueProjects() {
        return projectRepository.findOverdueProjects(LocalDate.now());
    }
    
    public List<Project> getProjectsDueSoon(int days) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);
        return projectRepository.findProjectsDueSoon(today, endDate);
    }
    
    public Project updateProjectStatus(Long id, String status) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        
        project.setStatus(status);
        project.setUpdatedAt(LocalDateTime.now());
        
        return projectRepository.save(project);
    }
}
