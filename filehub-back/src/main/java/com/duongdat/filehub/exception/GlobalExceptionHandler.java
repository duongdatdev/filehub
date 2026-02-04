package com.duongdat.filehub.exception;

import com.duongdat.filehub.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle validation exceptions
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(ValidationException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Handle file upload exceptions
     */
    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ApiResponse<Void>> handleFileUploadException(FileUploadException ex) {
        log.error("File upload error: {}", ex.getMessage(), ex);
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Handle database constraint violations with user-friendly messages
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("Database constraint violation: {}", ex.getMessage());
        
        String message = ex.getMessage();
        String userFriendlyMessage = "Invalid data error";
        
        if (message != null) {
            // Check for specific constraint violations
            if (message.contains("department_id") && message.contains("cannot be null")) {
                userFriendlyMessage = "Department is required. Please select a department.";
            } else if (message.contains("file_type_id") && message.contains("cannot be null")) {
                userFriendlyMessage = "File type is required. Please select a file type.";
            } else if (message.contains("uploader_id") && message.contains("cannot be null")) {
                userFriendlyMessage = "User authentication error. Please login again.";
            } else if (message.contains("Duplicate entry") || message.contains("unique constraint")) {
                if (message.contains("file_hash")) {
                    userFriendlyMessage = "This file already exists in the system.";
                } else if (message.contains("stored_filename")) {
                    userFriendlyMessage = "File name already exists. Please try again.";
                } else {
                    userFriendlyMessage = "This data already exists in the system.";
                }
            } else if (message.contains("foreign key constraint")) {
                if (message.contains("department_id")) {
                    userFriendlyMessage = "The selected department does not exist or has been deleted.";
                } else if (message.contains("project_id")) {
                    userFriendlyMessage = "The selected project does not exist or has been deleted.";
                } else if (message.contains("file_type_id")) {
                    userFriendlyMessage = "The selected file type is invalid.";
                } else {
                    userFriendlyMessage = "Related data is invalid or has been deleted.";
                }
            }
        }
        
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(userFriendlyMessage));
    }

    /**
     * Handle validation errors from @Valid annotations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        log.warn("Validation failed: {}", errors);
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error("Validation failed", errors));
    }

    /**
     * Handle file size exceeded exception
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        log.warn("File size exceeded: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiResponse.error("File is too large. Please choose a smaller file."));
    }

    /**
     * Handle access denied exception
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("You don't have permission to perform this action."));
    }

    /**
     * Handle generic runtime exceptions
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception: {}", ex.getMessage(), ex);
        
        // Check if it's a known user-friendly message
        String message = ex.getMessage();
        if (message != null && (
                message.contains("don't have permission") ||
                message.contains("must be assigned") ||
                message.contains("not found") ||
                message.contains("already exists") ||
                message.contains("not authenticated") ||
                message.contains("is required"))) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(message));
        }
        
        // Generic error for unexpected runtime exceptions
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An error occurred. Please try again later."));
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred. Please contact the administrator."));
    }
}
