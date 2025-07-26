package com.duongdat.filehub.repository;

import com.duongdat.filehub.entity.FileAccessLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FileAccessLogRepository extends JpaRepository<FileAccessLog, Long> {
    
    List<FileAccessLog> findByFileIdOrderByCreatedAtDesc(Long fileId);
    
    List<FileAccessLog> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    Page<FileAccessLog> findByFileIdOrderByCreatedAtDesc(Long fileId, Pageable pageable);
    
    Page<FileAccessLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    @Query("SELECT fal FROM FileAccessLog fal WHERE fal.createdAt BETWEEN :startDate AND :endDate ORDER BY fal.createdAt DESC")
    List<FileAccessLog> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT fal.action, COUNT(fal) FROM FileAccessLog fal WHERE fal.fileId = :fileId GROUP BY fal.action")
    List<Object[]> getFileAccessStatistics(@Param("fileId") Long fileId);
    
    @Query("SELECT COUNT(fal) FROM FileAccessLog fal WHERE fal.fileId = :fileId AND fal.action = :action")
    Long countByFileIdAndAction(@Param("fileId") Long fileId, @Param("action") String action);
    
    @Query("SELECT fal FROM FileAccessLog fal WHERE fal.userId = :userId AND fal.createdAt >= :since ORDER BY fal.createdAt DESC")
    List<FileAccessLog> findRecentUserActivity(@Param("userId") Long userId, @Param("since") LocalDateTime since);
}
