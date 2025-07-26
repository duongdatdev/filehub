package com.duongdat.filehub.repository;

import com.duongdat.filehub.entity.FileShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FileShareRepository extends JpaRepository<FileShare, Long> {
    
    List<FileShare> findByFileIdAndIsActiveTrueOrderByCreatedAtDesc(Long fileId);
    
    List<FileShare> findBySharedWithAndIsActiveTrueOrderByCreatedAtDesc(Long sharedWith);
    
    List<FileShare> findBySharedByAndIsActiveTrueOrderByCreatedAtDesc(Long sharedBy);
    
    Optional<FileShare> findByFileIdAndSharedByAndSharedWith(Long fileId, Long sharedBy, Long sharedWith);
    
    @Query("SELECT fs FROM FileShare fs WHERE fs.sharedWith = :userId AND fs.isActive = true AND (fs.expiresAt IS NULL OR fs.expiresAt > :now)")
    List<FileShare> findActiveSharesForUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    @Query("SELECT fs FROM FileShare fs WHERE fs.fileId = :fileId AND fs.isActive = true AND (fs.expiresAt IS NULL OR fs.expiresAt > :now)")
    List<FileShare> findActiveSharesForFile(@Param("fileId") Long fileId, @Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(fs) FROM FileShare fs WHERE fs.fileId = :fileId AND fs.isActive = true")
    Long countActiveSharesForFile(@Param("fileId") Long fileId);
    
    @Query("SELECT fs FROM FileShare fs WHERE fs.expiresAt <= :now AND fs.isActive = true")
    List<FileShare> findExpiredShares(@Param("now") LocalDateTime now);
    
    boolean existsByFileIdAndSharedWithAndIsActiveTrue(Long fileId, Long sharedWith);
}
