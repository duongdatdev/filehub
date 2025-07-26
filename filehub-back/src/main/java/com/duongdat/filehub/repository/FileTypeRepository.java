package com.duongdat.filehub.repository;

import com.duongdat.filehub.entity.FileType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileTypeRepository extends JpaRepository<FileType, Long> {
    
    Optional<FileType> findByName(String name);
    
    @Query("SELECT ft FROM FileType ft ORDER BY ft.name ASC")
    List<FileType> findAllOrderByName();
    
    @Query("SELECT ft.allowedExtensions FROM FileType ft WHERE ft.name = :typeName")
    String findAllowedExtensionsByTypeName(String typeName);
    
    @Query("SELECT ft FROM FileType ft WHERE ft.maxSize >= :fileSize")
    List<FileType> findByMaxSizeGreaterThanEqual(Long fileSize);
}
