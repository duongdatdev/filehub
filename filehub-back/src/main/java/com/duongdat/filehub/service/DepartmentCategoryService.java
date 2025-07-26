package com.duongdat.filehub.service;

import com.duongdat.filehub.entity.DepartmentCategory;
import com.duongdat.filehub.repository.DepartmentCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartmentCategoryService {
    
    private final DepartmentCategoryRepository departmentCategoryRepository;
    
    public List<DepartmentCategory> getCategoriesByDepartment(Long departmentId) {
        return departmentCategoryRepository.findActiveCategoriesByDepartment(departmentId);
    }
    
    public List<DepartmentCategory> getAllCategoriesByDepartment(Long departmentId) {
        return departmentCategoryRepository.findByDepartmentIdOrderByDisplayOrderAsc(departmentId);
    }
    
    public Optional<DepartmentCategory> getCategoryById(Long id) {
        return departmentCategoryRepository.findById(id);
    }
    
    public DepartmentCategory createCategory(DepartmentCategory category) {
        // Check if category name already exists in the department
        if (departmentCategoryRepository.existsByDepartmentIdAndName(category.getDepartmentId(), category.getName())) {
            throw new RuntimeException("Category name already exists in this department");
        }
        return departmentCategoryRepository.save(category);
    }
    
    public DepartmentCategory updateCategory(Long id, DepartmentCategory updatedCategory) {
        return departmentCategoryRepository.findById(id)
            .map(existingCategory -> {
                // Check if new name conflicts with existing category in same department
                if (!existingCategory.getName().equals(updatedCategory.getName()) &&
                    departmentCategoryRepository.existsByDepartmentIdAndName(
                        existingCategory.getDepartmentId(), updatedCategory.getName())) {
                    throw new RuntimeException("Category name already exists in this department");
                }
                
                existingCategory.setName(updatedCategory.getName());
                existingCategory.setDescription(updatedCategory.getDescription());
                existingCategory.setColor(updatedCategory.getColor());
                existingCategory.setIcon(updatedCategory.getIcon());
                existingCategory.setDisplayOrder(updatedCategory.getDisplayOrder());
                existingCategory.setIsActive(updatedCategory.getIsActive());
                return departmentCategoryRepository.save(existingCategory);
            })
            .orElseThrow(() -> new RuntimeException("Department category not found with id: " + id));
    }
    
    public void deleteCategory(Long id) {
        departmentCategoryRepository.deleteById(id);
    }
    
    public void deactivateCategory(Long id) {
        departmentCategoryRepository.findById(id)
            .ifPresentOrElse(
                category -> {
                    category.setIsActive(false);
                    departmentCategoryRepository.save(category);
                },
                () -> {
                    throw new RuntimeException("Department category not found with id: " + id);
                }
            );
    }
    
    public Long getActiveCategoryCountByDepartment(Long departmentId) {
        return departmentCategoryRepository.countActiveCategoriesByDepartment(departmentId);
    }
}
