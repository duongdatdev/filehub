package com.duongdat.filehub.repository;

import com.duongdat.filehub.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    List<Project> findAllByOrderByCreatedAtDesc();
    
    List<Project> findByDepartmentIdOrderByCreatedAtDesc(Long departmentId);
    
    List<Project> findByStatusOrderByCreatedAtDesc(String status);
    
    @Query("SELECT p FROM Project p WHERE " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:departmentId IS NULL OR p.departmentId = :departmentId) AND " +
           "(:status IS NULL OR p.status = :status)")
    Page<Project> findProjectsWithFilters(
        @Param("name") String name,
        @Param("departmentId") Long departmentId,
        @Param("status") String status,
        Pageable pageable
    );
    
    @Query("SELECT COUNT(f) FROM File f WHERE f.projectId = :projectId AND f.isDeleted = false")
    Long countFilesByProjectId(@Param("projectId") Long projectId);
    
    @Query("SELECT p FROM Project p JOIN UserProject up ON p.id = up.projectId " +
           "WHERE up.userId = :userId AND up.isActive = true AND p.status != 'ARCHIVED' " +
           "ORDER BY p.createdAt DESC")
    List<Project> findProjectsByUserId(@Param("userId") Long userId);
}
