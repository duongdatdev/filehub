package com.duongdat.filehub.repository;

import com.duongdat.filehub.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    List<Department> findByIsActiveTrueOrderByName();
    
    Optional<Department> findByNameAndIsActiveTrue(String name);
    
    List<Department> findByParentIdAndIsActiveTrueOrderByName(Long parentId);
    
    List<Department> findByParentIdIsNullAndIsActiveTrueOrderByName();
    
    List<Department> findByManagerIdAndIsActiveTrue(Long managerId);
    
    @Query("SELECT d FROM Department d WHERE " +
           "(:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:managerId IS NULL OR d.managerId = :managerId) AND " +
           "(:isActive IS NULL OR d.isActive = :isActive)")
    Page<Department> findDepartmentsWithFilters(
        @Param("name") String name,
        @Param("managerId") Long managerId,
        @Param("isActive") Boolean isActive,
        Pageable pageable
    );
    
    @Query("SELECT COUNT(ud) FROM UserDepartment ud WHERE ud.departmentId = :departmentId AND ud.isActive = true")
    Long countUsersByDepartmentId(@Param("departmentId") Long departmentId);
    
    @Query("SELECT COUNT(p) FROM Project p WHERE p.departmentId = :departmentId")
    Long countActiveProjectsByDepartmentId(@Param("departmentId") Long departmentId);
    
    @Query("SELECT d FROM Department d JOIN UserDepartment ud ON d.id = ud.departmentId " +
           "WHERE ud.userId = :userId AND ud.isActive = true AND d.isActive = true " +
           "ORDER BY d.name")
    List<Department> findDepartmentsByUserId(@Param("userId") Long userId);
}
