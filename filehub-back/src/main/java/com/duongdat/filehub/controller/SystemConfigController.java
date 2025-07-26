package com.duongdat.filehub.controller;

import com.duongdat.filehub.dto.response.ApiResponse;
import com.duongdat.filehub.entity.SystemConfig;
import com.duongdat.filehub.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system-configs")
@RequiredArgsConstructor
public class SystemConfigController {
    
    private final SystemConfigService systemConfigService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<SystemConfig>>> getAllConfigs() {
        try {
            List<SystemConfig> configs = systemConfigService.getAllConfigs();
            return ResponseEntity.ok(ApiResponse.success("System configurations retrieved successfully", configs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/public")
    public ResponseEntity<ApiResponse<List<SystemConfig>>> getPublicConfigs() {
        try {
            List<SystemConfig> configs = systemConfigService.getPublicConfigs();
            return ResponseEntity.ok(ApiResponse.success("Public configurations retrieved successfully", configs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{configKey}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SystemConfig>> getConfigByKey(@PathVariable String configKey) {
        try {
            SystemConfig config = systemConfigService.getConfigByKey(configKey)
                    .orElseThrow(() -> new RuntimeException("Configuration not found"));
            return ResponseEntity.ok(ApiResponse.success("Configuration retrieved successfully", config));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{configKey}/value")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> getConfigValue(@PathVariable String configKey) {
        try {
            String value = systemConfigService.getConfigValue(configKey)
                    .orElseThrow(() -> new RuntimeException("Configuration not found"));
            return ResponseEntity.ok(ApiResponse.success("Configuration value retrieved successfully", value));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SystemConfig>> createOrUpdateConfig(@RequestBody Map<String, Object> configData) {
        try {
            String configKey = (String) configData.get("configKey");
            String configValue = (String) configData.get("configValue");
            String dataType = (String) configData.getOrDefault("dataType", "STRING");
            String description = (String) configData.get("description");
            Boolean isPublic = (Boolean) configData.getOrDefault("isPublic", false);
            
            SystemConfig config = systemConfigService.createOrUpdateConfig(configKey, configValue, dataType, description, isPublic);
            return ResponseEntity.ok(ApiResponse.success("Configuration saved successfully", config));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{configKey}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SystemConfig>> updateConfigValue(@PathVariable String configKey, @RequestBody Map<String, String> data) {
        try {
            String configValue = data.get("configValue");
            SystemConfig config = systemConfigService.updateConfig(configKey, configValue);
            return ResponseEntity.ok(ApiResponse.success("Configuration updated successfully", config));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{configKey}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteConfig(@PathVariable String configKey) {
        try {
            systemConfigService.deleteConfig(configKey);
            return ResponseEntity.ok(ApiResponse.success("Configuration deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/type/{dataType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<SystemConfig>>> getConfigsByDataType(@PathVariable String dataType) {
        try {
            List<SystemConfig> configs = systemConfigService.getConfigsByDataType(dataType);
            return ResponseEntity.ok(ApiResponse.success("Configurations retrieved successfully", configs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
