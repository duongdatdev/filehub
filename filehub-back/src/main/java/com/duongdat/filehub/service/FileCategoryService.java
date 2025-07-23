package com.duongdat.filehub.service;

import com.duongdat.filehub.entity.FileCategory;
import com.duongdat.filehub.repository.FileCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileCategoryService {
    
    private final FileCategoryRepository fileCategoryRepository;
    
    public List<FileCategory> getAllActiveCategories() {
        return fileCategoryRepository.findByIsActiveTrueOrderByDisplayOrder();
    }
    
    public Optional<FileCategory> getCategoryById(Long id) {
        return fileCategoryRepository.findById(id);
    }
    
    public Optional<FileCategory> getCategoryByName(String name) {
        return fileCategoryRepository.findByNameAndIsActiveTrue(name);
    }
    
    public List<FileCategory> getSubCategories(Long parentId) {
        return fileCategoryRepository.findByParentIdAndIsActiveTrueOrderByDisplayOrder(parentId);
    }
    
    public List<FileCategory> getRootCategories() {
        return fileCategoryRepository.findByParentIdIsNullAndIsActiveTrueOrderByDisplayOrder();
    }
}
