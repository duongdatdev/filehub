package com.duongdat.filehub.repository;

import com.duongdat.filehub.entity.DepartmentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentCategoryRepository extends JpaRepository<DepartmentCategory, Long> {
    
    List<DepartmentCategory> findByDepartmentIdAndIsActiveTrue(Long departmentId);
    
    List<DepartmentCategory> findByDepartmentIdOrderByDisplayOrderAsc(Long departmentId);
    
    Optional<DepartmentCategory> findByDepartmentIdAndName(Long departmentId, String name);
    
    @Query("SELECT dc FROM DepartmentCategory dc WHERE dc.departmentId = :departmentId AND dc.isActive = true ORDER BY dc.displayOrder ASC")
    List<DepartmentCategory> findActiveCategoriesByDepartment(@Param("departmentId") Long departmentId);
    
    @Query("SELECT COUNT(dc) FROM DepartmentCategory dc WHERE dc.departmentId = :departmentId AND dc.isActive = true")
    Long countActiveCategoriesByDepartment(@Param("departmentId") Long departmentId);
    
    boolean existsByDepartmentIdAndName(Long departmentId, String name);
}
