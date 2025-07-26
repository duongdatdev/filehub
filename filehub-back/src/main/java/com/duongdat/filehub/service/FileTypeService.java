package com.duongdat.filehub.service;

import com.duongdat.filehub.entity.FileType;
import com.duongdat.filehub.repository.FileTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileTypeService {
    
    private final FileTypeRepository fileTypeRepository;
    
    public List<FileType> getAllFileTypes() {
        return fileTypeRepository.findAllOrderByName();
    }
    
    public Optional<FileType> getFileTypeById(Long id) {
        return fileTypeRepository.findById(id);
    }
    
    public Optional<FileType> getFileTypeByName(String name) {
        return fileTypeRepository.findByName(name);
    }
    
    public FileType createFileType(FileType fileType) {
        return fileTypeRepository.save(fileType);
    }
    
    public FileType updateFileType(Long id, FileType updatedFileType) {
        return fileTypeRepository.findById(id)
            .map(existingFileType -> {
                existingFileType.setName(updatedFileType.getName());
                existingFileType.setDescription(updatedFileType.getDescription());
                existingFileType.setAllowedExtensions(updatedFileType.getAllowedExtensions());
                existingFileType.setColor(updatedFileType.getColor());
                existingFileType.setIcon(updatedFileType.getIcon());
                existingFileType.setMaxSize(updatedFileType.getMaxSize());
                return fileTypeRepository.save(existingFileType);
            })
            .orElseThrow(() -> new RuntimeException("FileType not found with id: " + id));
    }
    
    public void deleteFileType(Long id) {
        fileTypeRepository.deleteById(id);
    }
    
    public List<FileType> getFileTypesBySizeLimit(Long fileSize) {
        return fileTypeRepository.findByMaxSizeGreaterThanEqual(fileSize);
    }
    
    public String getAllowedExtensions(String typeName) {
        return fileTypeRepository.findAllowedExtensionsByTypeName(typeName);
    }
}
