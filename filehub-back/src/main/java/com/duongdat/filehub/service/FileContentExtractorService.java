package com.duongdat.filehub.service;

import com.duongdat.filehub.config.GeminiProperties;
import lombok.extern.slf4j.Slf4j;
// import org.apache.pdfbox.pdmodel.PDDocument;
// import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
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
    
    private final GeminiProperties geminiProperties;
    
    public FileContentExtractorService(GeminiProperties geminiProperties) {
        this.geminiProperties = geminiProperties;
    }
    
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
                case "docx":
                    return extractDocxContent(file.getInputStream());
                case "doc":
                    return extractDocContent(file.getInputStream());
                // case "pdf":
                //     return extractPdfContent(file.getInputStream());
                case "xlsx":
                    return extractXlsxContent(file.getInputStream());
                case "pptx":
                    return extractPptxContent(file.getInputStream());
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
            while ((line = reader.readLine()) != null && content.length() < geminiProperties.getMaxContentLength()) {
                content.append(line).append("\n");
            }
        }
        
        return truncateContent(content.toString());
    }
    
    private String truncateContent(String content) {
        if (content == null) {
            return "";
        }
        
        if (content.length() <= geminiProperties.getMaxContentLength()) {
            return content;
        }
        
        // Truncate and add indication
        return content.substring(0, geminiProperties.getMaxContentLength()) + "\n\n[Content truncated...]";
    }
    
    private String extractDocxContent(InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            String content = extractor.getText();
            log.debug("Extracted DOCX content: {} characters", content.length());
            return truncateContent(content);
        }
    }
    
    private String extractDocContent(InputStream inputStream) throws IOException {
        try (HWPFDocument document = new HWPFDocument(inputStream);
             WordExtractor extractor = new WordExtractor(document)) {
            String content = extractor.getText();
            log.debug("Extracted DOC content: {} characters", content.length());
            return truncateContent(content);
        }
    }
    
    /*
    private String extractPdfContent(InputStream inputStream) throws IOException {
        try {
            byte[] pdfBytes = inputStream.readAllBytes();
            try (PDDocument document = PDDocument.load(pdfBytes)) {
                PDFTextStripper stripper = new PDFTextStripper();
                // Limit to first few pages for performance
                stripper.setEndPage(Math.min(5, document.getNumberOfPages()));
                String content = stripper.getText(document);
                log.debug("Extracted PDF content: {} characters from {} pages", 
                    content.length(), Math.min(5, document.getNumberOfPages()));
                return truncateContent(content);
            }
        } catch (Exception e) {
            log.warn("Failed to extract PDF content: {}", e.getMessage());
            return "";
        }
    }
    */
    
    private String extractXlsxContent(InputStream inputStream) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            StringBuilder content = new StringBuilder();
            
            // Extract from first few sheets
            int maxSheets = Math.min(3, workbook.getNumberOfSheets());
            for (int i = 0; i < maxSheets; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                content.append("Sheet: ").append(sheet.getSheetName()).append("\n");
                
                // Extract first few rows
                int maxRows = Math.min(50, sheet.getLastRowNum() + 1);
                for (int rowNum = 0; rowNum < maxRows; rowNum++) {
                    Row row = sheet.getRow(rowNum);
                    if (row != null) {
                        for (Cell cell : row) {
                            content.append(cell.toString()).append("\t");
                        }
                        content.append("\n");
                    }
                    
                    if (content.length() > geminiProperties.getMaxContentLength()) {
                        break;
                    }
                }
                
                if (content.length() > geminiProperties.getMaxContentLength()) {
                    break;
                }
            }
            
            log.debug("Extracted XLSX content: {} characters from {} sheets", 
                content.length(), maxSheets);
            return truncateContent(content.toString());
        }
    }
    
    private String extractPptxContent(InputStream inputStream) throws IOException {
        try (XMLSlideShow slideShow = new XMLSlideShow(inputStream)) {
            StringBuilder content = new StringBuilder();
            
            // Extract text from slides manually
            for (int i = 0; i < Math.min(10, slideShow.getSlides().size()); i++) {
                content.append("Slide ").append(i + 1).append(":\n");
                // Basic text extraction - this is a simplified approach
                content.append(slideShow.getSlides().get(i).toString()).append("\n\n");
                
                if (content.length() > geminiProperties.getMaxContentLength()) {
                    break;
                }
            }
            
            log.debug("Extracted PPTX content: {} characters from {} slides", 
                content.length(), Math.min(10, slideShow.getSlides().size()));
            return truncateContent(content.toString());
        }
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
               extension.equals("c") || extension.equals("h") ||
               // Office documents
               extension.equals("docx") || extension.equals("doc") ||
               extension.equals("xlsx") || extension.equals("pptx");
               // PDF - temporarily disabled
               // extension.equals("pdf");
    }
}
