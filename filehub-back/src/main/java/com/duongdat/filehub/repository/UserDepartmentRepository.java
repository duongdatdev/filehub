package com.duongdat.filehub.repository;

import com.duongdat.filehub.entity.UserDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDepartmentRepository extends JpaRepository<UserDepartment, Long> {
    
    /**
     * Find all active department assignments for a user
     */
    List<UserDepartment> findByUserIdAndIsActiveTrue(Long userId);
    
    /**
     * Find all active user assignments for a department
     */
    List<UserDepartment> findByDepartmentIdAndIsActiveTrue(Long departmentId);
    
    /**
     * Find specific user-department assignment
     */
    Optional<UserDepartment> findByUserIdAndDepartmentId(Long userId, Long departmentId);
    
    /**
     * Check if user is assigned to a specific department
     */
    boolean existsByUserIdAndDepartmentIdAndIsActiveTrue(Long userId, Long departmentId);
    
    /**
     * Check if user has manager role in any department
     */
    @Query("SELECT COUNT(ud) > 0 FROM UserDepartment ud WHERE ud.userId = :userId AND ud.role = 'MANAGER' AND ud.isActive = true")
    boolean isUserDepartmentManager(@Param("userId") Long userId);
    
    /**
     * Check if user has manager role in specific department
     */
    @Query("SELECT COUNT(ud) > 0 FROM UserDepartment ud WHERE ud.userId = :userId AND ud.departmentId = :departmentId AND ud.role = 'MANAGER' AND ud.isActive = true")
    boolean isUserManagerOfDepartment(@Param("userId") Long userId, @Param("departmentId") Long departmentId);
    
    /**
     * Get all departments where user has manager role
     */
    @Query("SELECT ud FROM UserDepartment ud WHERE ud.userId = :userId AND ud.role IN ('MANAGER', 'DEPUTY_MANAGER') AND ud.isActive = true")
    List<UserDepartment> findManagerRolesForUser(@Param("userId") Long userId);
    
    /**
     * Get department IDs for a user
     */
    @Query("SELECT ud.departmentId FROM UserDepartment ud WHERE ud.userId = :userId AND ud.isActive = true")
    List<Long> findDepartmentIdsByUserId(@Param("userId") Long userId);
}
