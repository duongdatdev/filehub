package com.duongdat.filehub.service;

import com.duongdat.filehub.dto.response.PageResponse;
import com.duongdat.filehub.entity.Department;
import com.duongdat.filehub.repository.DepartmentRepository;
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
public class DepartmentService {
    
    private final DepartmentRepository departmentRepository;
    private final SecurityUtil securityUtil;
    
    public List<Department> getAllActiveDepartments() {
        return departmentRepository.findByIsActiveTrueOrderByName();
    }
    
    public PageResponse<Department> getDepartmentsWithFilters(String name, Long managerId, Boolean isActive, 
                                                            int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Department> departmentPage = departmentRepository.findDepartmentsWithFilters(
            name, managerId, isActive, pageable);
        
        return new PageResponse<Department>(
            departmentPage.getContent(),
            departmentPage.getNumber(),
            departmentPage.getSize(),
            departmentPage.getTotalElements(),
            departmentPage.getTotalPages(),
            departmentPage.isFirst(),
            departmentPage.isLast(),
            departmentPage.hasNext(),
            departmentPage.hasPrevious()
        );
    }
    
    public Optional<Department> getDepartmentById(Long id) {
        return departmentRepository.findById(id);
    }
    
    public Optional<Department> getDepartmentByName(String name) {
        return departmentRepository.findByNameAndIsActiveTrue(name);
    }
    
    public List<Department> getSubDepartments(Long parentId) {
        return departmentRepository.findByParentIdAndIsActiveTrueOrderByName(parentId);
    }
    
    public List<Department> getRootDepartments() {
        return departmentRepository.findByParentIdIsNullAndIsActiveTrueOrderByName();
    }
    
    public List<Department> getDepartmentsByManager(Long managerId) {
        return departmentRepository.findByManagerIdAndIsActiveTrue(managerId);
    }
    
    public Department createDepartment(Department department) {
        department.setCreatedAt(LocalDateTime.now());
        department.setUpdatedAt(LocalDateTime.now());
        return departmentRepository.save(department);
    }
    
    public Department updateDepartment(Long id, Department departmentDetails) {
        Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Department not found"));
        
        department.setName(departmentDetails.getName());
        department.setDescription(departmentDetails.getDescription());
        department.setManagerId(departmentDetails.getManagerId());
        department.setParentId(departmentDetails.getParentId());
        department.setUpdatedAt(LocalDateTime.now());
        
        return departmentRepository.save(department);
    }
    
    public boolean deleteDepartment(Long id) {
        Optional<Department> departmentOpt = departmentRepository.findById(id);
        if (departmentOpt.isPresent()) {
            Department department = departmentOpt.get();
            
            // Check if department has users
            Long userCount = departmentRepository.countUsersByDepartmentId(id);
            if (userCount > 0) {
                throw new RuntimeException("Cannot delete department with assigned users");
            }
            
            // Check if department has active projects
            Long projectCount = departmentRepository.countActiveProjectsByDepartmentId(id);
            if (projectCount > 0) {
                throw new RuntimeException("Cannot delete department with active projects");
            }
            
            // Soft delete
            department.setIsActive(false);
            department.setUpdatedAt(LocalDateTime.now());
            departmentRepository.save(department);
            return true;
        }
        return false;
    }
    
    public Long getUserCountByDepartment(Long departmentId) {
        return departmentRepository.countUsersByDepartmentId(departmentId);
    }
    
    public Long getActiveProjectCountByDepartment(Long departmentId) {
        return departmentRepository.countActiveProjectsByDepartmentId(departmentId);
    }
    
    public List<Department> getCurrentUserDepartments() {
        Long currentUserId = securityUtil.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));
        
        return departmentRepository.findDepartmentsByUserId(currentUserId);
    }
}
