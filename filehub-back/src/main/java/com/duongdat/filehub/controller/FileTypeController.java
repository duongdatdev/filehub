package com.duongdat.filehub.controller;

import com.duongdat.filehub.dto.response.ApiResponse;
import com.duongdat.filehub.entity.FileType;
import com.duongdat.filehub.service.FileTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/file-types")
@RequiredArgsConstructor
public class FileTypeController {
    
    private final FileTypeService fileTypeService;
    
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<FileType>>> getAllFileTypes() {
        try {
            List<FileType> fileTypes = fileTypeService.getAllFileTypes();
            return ResponseEntity.ok(ApiResponse.success("File types retrieved successfully", fileTypes));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FileType>> getFileTypeById(@PathVariable Long id) {
        try {
            FileType fileType = fileTypeService.getFileTypeById(id)
                    .orElseThrow(() -> new RuntimeException("File type not found"));
            return ResponseEntity.ok(ApiResponse.success("File type retrieved successfully", fileType));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/name/{name}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FileType>> getFileTypeByName(@PathVariable String name) {
        try {
            FileType fileType = fileTypeService.getFileTypeByName(name)
                    .orElseThrow(() -> new RuntimeException("File type not found"));
            return ResponseEntity.ok(ApiResponse.success("File type retrieved successfully", fileType));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FileType>> createFileType(@RequestBody FileType fileType) {
        try {
            FileType createdFileType = fileTypeService.createFileType(fileType);
            return ResponseEntity.ok(ApiResponse.success("File type created successfully", createdFileType));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FileType>> updateFileType(@PathVariable Long id, @RequestBody FileType fileType) {
        try {
            FileType updatedFileType = fileTypeService.updateFileType(id, fileType);
            return ResponseEntity.ok(ApiResponse.success("File type updated successfully", updatedFileType));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteFileType(@PathVariable Long id) {
        try {
            fileTypeService.deleteFileType(id);
            return ResponseEntity.ok(ApiResponse.success("File type deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{typeName}/extensions")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> getAllowedExtensions(@PathVariable String typeName) {
        try {
            String extensions = fileTypeService.getAllowedExtensions(typeName);
            return ResponseEntity.ok(ApiResponse.success("Extensions retrieved successfully", extensions));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
