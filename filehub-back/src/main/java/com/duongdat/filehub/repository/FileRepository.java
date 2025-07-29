package com.duongdat.filehub.repository;

import com.duongdat.filehub.entity.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    
    // Basic file queries
    List<File> findByUploaderIdAndIsDeletedFalse(Long uploaderId);
    
    Page<File> findByUploaderIdAndIsDeletedFalse(Long uploaderId, Pageable pageable);
    
    Optional<File> findByIdAndUploaderIdAndIsDeletedFalse(Long id, Long uploaderId);
    
    Optional<File> findByFileHash(String fileHash);
    
    Optional<File> findByDriveFileId(String driveFileId);
    
    // Department-based queries
    List<File> findByDepartmentIdAndIsDeletedFalse(Long departmentId);
    
    // Department files that don't belong to projects
    List<File> findByDepartmentIdAndProjectIdIsNullAndIsDeletedFalse(Long departmentId);
    
    List<File> findByDepartmentCategoryIdAndIsDeletedFalse(Long departmentCategoryId);
    
    // Project-based queries
    List<File> findByProjectIdAndIsDeletedFalse(Long projectId);
    
    // File type queries
    List<File> findByFileTypeIdAndIsDeletedFalse(Long fileTypeId);
    
    // Visibility queries
    List<File> findByVisibilityAndIsDeletedFalse(String visibility);
    
    @Query("SELECT f FROM File f WHERE f.departmentId = :departmentId AND f.visibility = 'DEPARTMENT' AND f.isDeleted = false")
    List<File> findDepartmentVisibleFiles(@Param("departmentId") Long departmentId);
    
    @Query("SELECT f FROM File f WHERE f.visibility = 'PUBLIC' AND f.isDeleted = false")
    List<File> findPublicFiles();
    
    // Complex filter query for users
    @Query("SELECT f FROM File f WHERE f.uploaderId = :uploaderId AND f.isDeleted = false AND " +
           "(:filename IS NULL OR LOWER(f.originalFilename) LIKE LOWER(CONCAT('%', :filename, '%'))) AND " +
           "(:departmentCategoryId IS NULL OR f.departmentCategoryId = :departmentCategoryId) AND " +
           "(:departmentId IS NULL OR f.departmentId = :departmentId) AND " +
           "(:projectId IS NULL OR f.projectId = :projectId) AND " +
           "(:fileTypeId IS NULL OR f.fileTypeId = :fileTypeId) AND " +
           "(:contentType IS NULL OR f.contentType LIKE CONCAT(:contentType, '%'))")
    Page<File> findFilesWithFilters(@Param("uploaderId") Long uploaderId,
                                  @Param("filename") String filename,
                                  @Param("departmentCategoryId") Long departmentCategoryId,
                                  @Param("departmentId") Long departmentId,
                                  @Param("projectId") Long projectId,
                                  @Param("fileTypeId") Long fileTypeId,
                                  @Param("contentType") String contentType,
                                  Pageable pageable);
    
    // Admin query for all files with filters
    @Query("SELECT f FROM File f WHERE f.isDeleted = false AND " +
           "(:filename IS NULL OR LOWER(f.originalFilename) LIKE LOWER(CONCAT('%', :filename, '%'))) AND " +
           "(:departmentCategoryId IS NULL OR f.departmentCategoryId = :departmentCategoryId) AND " +
           "(:departmentId IS NULL OR f.departmentId = :departmentId) AND " +
           "(:projectId IS NULL OR f.projectId = :projectId) AND " +
           "(:uploaderId IS NULL OR f.uploaderId = :uploaderId) AND " +
           "(:fileTypeId IS NULL OR f.fileTypeId = :fileTypeId) AND " +
           "(:contentType IS NULL OR f.contentType LIKE CONCAT(:contentType, '%'))")
    Page<File> findAllFilesWithFilters(@Param("filename") String filename,
                                     @Param("departmentCategoryId") Long departmentCategoryId,
                                     @Param("departmentId") Long departmentId,
                                     @Param("projectId") Long projectId,
                                     @Param("uploaderId") Long uploaderId,
                                     @Param("fileTypeId") Long fileTypeId,
                                     @Param("contentType") String contentType,
                                     Pageable pageable);
    
    // Query for files with authorization filters (for regular users)
    // Project files are only visible to project members, department files without projects are visible to department members
    @Query("SELECT f FROM File f WHERE f.isDeleted = false AND " +
           "(:filename IS NULL OR LOWER(f.originalFilename) LIKE LOWER(CONCAT('%', :filename, '%'))) AND " +
           "(:departmentCategoryId IS NULL OR f.departmentCategoryId = :departmentCategoryId) AND " +
           "(:departmentId IS NULL OR f.departmentId = :departmentId) AND " +
           "(:projectId IS NULL OR f.projectId = :projectId) AND " +
           "(:fileTypeId IS NULL OR f.fileTypeId = :fileTypeId) AND " +
           "(:contentType IS NULL OR f.contentType LIKE CONCAT(:contentType, '%')) AND " +
           "(f.visibility = 'PUBLIC' OR " +
           " (f.projectId IS NOT NULL AND f.projectId IN :accessibleProjectIds) OR " +
           " (f.projectId IS NULL AND f.departmentId IN :accessibleDepartmentIds))")
    Page<File> findFilesWithAuthorizationFilters(@Param("uploaderId") Long uploaderId,
                                                @Param("filename") String filename,
                                                @Param("departmentCategoryId") Long departmentCategoryId,
                                                @Param("departmentId") Long departmentId,
                                                @Param("projectId") Long projectId,
                                                @Param("fileTypeId") Long fileTypeId,
                                                @Param("contentType") String contentType,
                                                @Param("accessibleDepartmentIds") List<Long> accessibleDepartmentIds,
                                                @Param("accessibleProjectIds") List<Long> accessibleProjectIds,
                                                Pageable pageable);

    // Query for shared files (from all users) with authorization filters
    // Project files are only visible to project members, department files without projects are visible to department members
    @Query("SELECT f FROM File f WHERE f.isDeleted = false AND " +
           "(:filename IS NULL OR LOWER(f.originalFilename) LIKE LOWER(CONCAT('%', :filename, '%'))) AND " +
           "(:departmentCategoryId IS NULL OR f.departmentCategoryId = :departmentCategoryId) AND " +
           "(:departmentId IS NULL OR f.departmentId = :departmentId) AND " +
           "(:projectId IS NULL OR f.projectId = :projectId) AND " +
           "(:fileTypeId IS NULL OR f.fileTypeId = :fileTypeId) AND " +
           "(:contentType IS NULL OR f.contentType LIKE CONCAT(:contentType, '%')) AND " +
           "(f.visibility = 'PUBLIC' OR " +
           " (f.projectId IS NOT NULL AND f.projectId IN :accessibleProjectIds) OR " +
           " (f.projectId IS NULL AND f.departmentId IN :accessibleDepartmentIds))")
    Page<File> findSharedFilesWithAuthorizationFilters(@Param("filename") String filename,
                                                      @Param("departmentCategoryId") Long departmentCategoryId,
                                                      @Param("departmentId") Long departmentId,
                                                      @Param("projectId") Long projectId,
                                                      @Param("fileTypeId") Long fileTypeId,
                                                      @Param("contentType") String contentType,
                                                      @Param("accessibleDepartmentIds") List<Long> accessibleDepartmentIds,
                                                      @Param("accessibleProjectIds") List<Long> accessibleProjectIds,
                                                      Pageable pageable);

    // Query for shared files by department (excluding project files)
    @Query("SELECT f FROM File f WHERE f.isDeleted = false AND f.departmentId = :departmentId AND f.projectId IS NULL AND " +
           "(:filename IS NULL OR LOWER(f.originalFilename) LIKE LOWER(CONCAT('%', :filename, '%'))) AND " +
           "(:departmentCategoryId IS NULL OR f.departmentCategoryId = :departmentCategoryId) AND " +
           "(:fileTypeId IS NULL OR f.fileTypeId = :fileTypeId) AND " +
           "(:contentType IS NULL OR f.contentType LIKE CONCAT(:contentType, '%')) AND " +
           "(f.visibility = 'PUBLIC' OR f.visibility = 'DEPARTMENT')")
    Page<File> findSharedFilesByDepartment(@Param("departmentId") Long departmentId,
                                         @Param("filename") String filename,
                                         @Param("departmentCategoryId") Long departmentCategoryId,
                                         @Param("fileTypeId") Long fileTypeId,
                                         @Param("contentType") String contentType,
                                         Pageable pageable);

    // Query for shared files by project
    @Query("SELECT f FROM File f WHERE f.isDeleted = false AND f.projectId = :projectId AND " +
           "(:filename IS NULL OR LOWER(f.originalFilename) LIKE LOWER(CONCAT('%', :filename, '%'))) AND " +
           "(:fileTypeId IS NULL OR f.fileTypeId = :fileTypeId) AND " +
           "(:contentType IS NULL OR f.contentType LIKE CONCAT(:contentType, '%')) AND " +
           "(f.visibility = 'PUBLIC' OR f.visibility = 'DEPARTMENT' OR f.visibility = 'PROJECT')")
    Page<File> findSharedFilesByProject(@Param("projectId") Long projectId,
                                      @Param("filename") String filename,
                                      @Param("fileTypeId") Long fileTypeId,
                                      @Param("contentType") String contentType,
                                      Pageable pageable);
    
    // Statistics queries
    @Query("SELECT SUM(f.fileSize) FROM File f WHERE f.uploaderId = :uploaderId AND f.isDeleted = false")
    Long getTotalFileSizeByUser(@Param("uploaderId") Long uploaderId);
    
    @Query("SELECT COUNT(f) FROM File f WHERE f.uploaderId = :uploaderId AND f.isDeleted = false")
    Long getTotalFileCountByUser(@Param("uploaderId") Long uploaderId);
    
    @Query("SELECT COUNT(f) FROM File f WHERE f.departmentId = :departmentId AND f.isDeleted = false")
    Long getFileCountByDepartment(@Param("departmentId") Long departmentId);
    
    @Query("SELECT COUNT(f) FROM File f WHERE f.projectId = :projectId AND f.isDeleted = false")
    Long getFileCountByProject(@Param("projectId") Long projectId);
    
    @Query("SELECT COUNT(f) FROM File f WHERE f.fileTypeId = :fileTypeId AND f.isDeleted = false")
    Long getFileCountByFileType(@Param("fileTypeId") Long fileTypeId);
    
    // Recent files
    @Query("SELECT f FROM File f WHERE f.uploaderId = :uploaderId AND f.isDeleted = false ORDER BY f.uploadedAt DESC")
    List<File> findRecentFilesByUser(@Param("uploaderId") Long uploaderId, Pageable pageable);
    
    @Query("SELECT f FROM File f WHERE f.departmentId = :departmentId AND f.isDeleted = false ORDER BY f.uploadedAt DESC")
    List<File> findRecentFilesByDepartment(@Param("departmentId") Long departmentId, Pageable pageable);
}
