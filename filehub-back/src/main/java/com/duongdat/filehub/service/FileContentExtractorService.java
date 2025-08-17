package com.duongdat.filehub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class FileContentExtractorService {
    
    private static final int MAX_CONTENT_LENGTH = 50000; // 50KB max content
    
    public String extractTextContent(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            
            if (fileName == null) {
                return "";
            }
            
            String extension = getFileExtension(fileName).toLowerCase();
            
            switch (extension) {
                case "txt":
                case "md":
                case "csv":
                case "json":
                case "xml":
                case "log":
                case "yaml":
                case "yml":
                case "properties":
                case "sql":
                case "js":
                case "ts":
                case "html":
                case "css":
                case "java":
                case "py":
                case "php":
                case "cpp":
                case "c":
                case "h":
                    return extractPlainText(file.getInputStream());
                default:
                    log.debug("Unsupported file type for content extraction: {}", extension);
                    return "";
            }
            
        } catch (Exception e) {
            log.error("Error extracting content from file: {}", e.getMessage());
            return "";
        }
    }
    
    private String extractPlainText(InputStream inputStream) throws IOException {
        StringBuilder content = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            
            String line;
            while ((line = reader.readLine()) != null && content.length() < MAX_CONTENT_LENGTH) {
                content.append(line).append("\n");
            }
        }
        
        return truncateContent(content.toString());
    }
    
    private String truncateContent(String content) {
        if (content == null) {
            return "";
        }
        
        if (content.length() <= MAX_CONTENT_LENGTH) {
            return content;
        }
        
        // Truncate and add indication
        return content.substring(0, MAX_CONTENT_LENGTH) + "\n\n[Content truncated...]";
    }
    
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1) : "";
    }
    
    public boolean isContentExtractable(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        return extension.equals("txt") || extension.equals("md") || extension.equals("csv") ||
               extension.equals("json") || extension.equals("xml") || extension.equals("log") ||
               extension.equals("yaml") || extension.equals("yml") || extension.equals("properties") ||
               extension.equals("sql") || extension.equals("js") || extension.equals("ts") ||
               extension.equals("html") || extension.equals("css") || extension.equals("java") ||
               extension.equals("py") || extension.equals("php") || extension.equals("cpp") ||
               extension.equals("c") || extension.equals("h");
    }
}
