package com.duongdat.filehub.repository;

import com.duongdat.filehub.entity.UserProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProjectRepository extends JpaRepository<UserProject, Long> {
    
    /**
     * Find all active project assignments for a user
     */
    List<UserProject> findByUserIdAndIsActiveTrue(Long userId);
    
    /**
     * Find all active user assignments for a project
     */
    List<UserProject> findByProjectIdAndIsActiveTrue(Long projectId);
    
    /**
     * Find specific user-project assignment
     */
    Optional<UserProject> findByUserIdAndProjectId(Long userId, Long projectId);
    
    /**
     * Check if user is assigned to a specific project
     */
    boolean existsByUserIdAndProjectIdAndIsActiveTrue(Long userId, Long projectId);
    
    /**
     * Check if user has lead role in any project
     */
    @Query("SELECT COUNT(up) > 0 FROM UserProject up WHERE up.userId = :userId AND up.role = 'LEAD' AND up.isActive = true")
    boolean isUserProjectLead(@Param("userId") Long userId);
    
    /**
     * Check if user has lead role in specific project
     */
    @Query("SELECT COUNT(up) > 0 FROM UserProject up WHERE up.userId = :userId AND up.projectId = :projectId AND up.role = 'LEAD' AND up.isActive = true")
    boolean isUserLeadOfProject(@Param("userId") Long userId, @Param("projectId") Long projectId);
    
    /**
     * Get all projects where user has lead role
     */
    @Query("SELECT up FROM UserProject up WHERE up.userId = :userId AND up.role = 'LEAD' AND up.isActive = true")
    List<UserProject> findLeadRolesForUser(@Param("userId") Long userId);
    
    /**
     * Get project IDs for a user
     */
    @Query("SELECT up.projectId FROM UserProject up WHERE up.userId = :userId AND up.isActive = true")
    List<Long> findProjectIdsByUserId(@Param("userId") Long userId);
    
    /**
     * Get projects for user in specific department
     */
    @Query("SELECT up FROM UserProject up JOIN Project p ON up.projectId = p.id " +
           "WHERE up.userId = :userId AND p.departmentId = :departmentId AND up.isActive = true")
    List<UserProject> findUserProjectsInDepartment(@Param("userId") Long userId, @Param("departmentId") Long departmentId);
}
