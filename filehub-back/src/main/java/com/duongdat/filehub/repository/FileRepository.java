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
    
    List<File> findByUserIdAndIsDeletedFalse(Long userId);
    
    Page<File> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);
    
    Optional<File> findByIdAndUserIdAndIsDeletedFalse(Long id, Long userId);
    
    Optional<File> findByFileHash(String fileHash);
    
    Optional<File> findByDriveFileId(String driveFileId);
    
    List<File> findByCategoryIdAndIsDeletedFalse(Long categoryId);
    
    @Query("SELECT f FROM File f WHERE f.userId = :userId AND f.isDeleted = false AND " +
           "(:filename IS NULL OR LOWER(f.originalFilename) LIKE LOWER(CONCAT('%', :filename, '%'))) AND " +
           "(:categoryId IS NULL OR f.categoryId = :categoryId) AND " +
           "(:contentType IS NULL OR f.contentType LIKE CONCAT(:contentType, '%'))")
    Page<File> findFilesWithFilters(@Param("userId") Long userId,
                                  @Param("filename") String filename,
                                  @Param("categoryId") Long categoryId,
                                  @Param("contentType") String contentType,
                                  Pageable pageable);
    
    @Query("SELECT SUM(f.fileSize) FROM File f WHERE f.userId = :userId AND f.isDeleted = false")
    Long getTotalFileSizeByUser(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(f) FROM File f WHERE f.userId = :userId AND f.isDeleted = false")
    Long getTotalFileCountByUser(@Param("userId") Long userId);
}
