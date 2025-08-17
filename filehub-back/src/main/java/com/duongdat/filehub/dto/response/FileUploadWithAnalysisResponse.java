package com.duongdat.filehub.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadWithAnalysisResponse {
    
    private FileResponse fileResponse;
    private FileAnalysisResponse analysisResponse;
    private boolean analysisEnabled;
    private String analysisMessage;
    
    // Static factory methods for convenience
    public static FileUploadWithAnalysisResponse withAnalysis(FileResponse fileResponse, FileAnalysisResponse analysisResponse) {
        return new FileUploadWithAnalysisResponse(fileResponse, analysisResponse, true, "AI analysis completed successfully");
    }
    
    public static FileUploadWithAnalysisResponse withoutAnalysis(FileResponse fileResponse, String reason) {
        return new FileUploadWithAnalysisResponse(fileResponse, null, false, reason);
    }
    
    public static FileUploadWithAnalysisResponse withAnalysisError(FileResponse fileResponse, String errorMessage) {
        return new FileUploadWithAnalysisResponse(fileResponse, null, true, "AI analysis failed: " + errorMessage);
    }
}
