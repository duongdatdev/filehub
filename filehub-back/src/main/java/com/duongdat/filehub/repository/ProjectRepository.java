package com.duongdat.filehub.repository;

import com.duongdat.filehub.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    List<Project> findByIsActiveTrueOrderByCreatedAtDesc();
    
    List<Project> findByDepartmentIdAndIsActiveTrueOrderByCreatedAtDesc(Long departmentId);
    
    List<Project> findByManagerIdAndIsActiveTrueOrderByCreatedAtDesc(Long managerId);
    
    List<Project> findByStatusAndIsActiveTrueOrderByCreatedAtDesc(String status);
    
    List<Project> findByPriorityAndIsActiveTrueOrderByCreatedAtDesc(String priority);
    
    @Query("SELECT p FROM Project p WHERE " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:departmentId IS NULL OR p.departmentId = :departmentId) AND " +
           "(:managerId IS NULL OR p.managerId = :managerId) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:priority IS NULL OR p.priority = :priority) AND " +
           "(:isActive IS NULL OR p.isActive = :isActive)")
    Page<Project> findProjectsWithFilters(
        @Param("name") String name,
        @Param("departmentId") Long departmentId,
        @Param("managerId") Long managerId,
        @Param("status") String status,
        @Param("priority") String priority,
        @Param("isActive") Boolean isActive,
        Pageable pageable
    );
    
    @Query("SELECT COUNT(f) FROM File f WHERE f.projectId = :projectId AND f.isDeleted = false")
    Long countFilesByProjectId(@Param("projectId") Long projectId);
    
    @Query("SELECT p FROM Project p WHERE p.endDate < :currentDate AND p.status != 'COMPLETED' AND p.isActive = true")
    List<Project> findOverdueProjects(@Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT p FROM Project p WHERE p.endDate BETWEEN :startDate AND :endDate AND p.status != 'COMPLETED' AND p.isActive = true")
    List<Project> findProjectsDueSoon(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
