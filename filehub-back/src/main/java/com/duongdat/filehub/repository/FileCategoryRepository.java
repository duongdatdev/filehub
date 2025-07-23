package com.duongdat.filehub.repository;

import com.duongdat.filehub.entity.FileCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileCategoryRepository extends JpaRepository<FileCategory, Long> {
    
    List<FileCategory> findByIsActiveTrueOrderByDisplayOrder();
    
    Optional<FileCategory> findByNameAndIsActiveTrue(String name);
    
    List<FileCategory> findByParentIdAndIsActiveTrueOrderByDisplayOrder(Long parentId);
    
    List<FileCategory> findByParentIdIsNullAndIsActiveTrueOrderByDisplayOrder();
}
