package com.duongdat.filehub.service;

import com.duongdat.filehub.entity.SystemConfig;
import com.duongdat.filehub.repository.SystemConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SystemConfigService {
    
    private final SystemConfigRepository systemConfigRepository;
    
    public List<SystemConfig> getAllConfigs() {
        return systemConfigRepository.findAll();
    }
    
    public List<SystemConfig> getPublicConfigs() {
        return systemConfigRepository.findPublicConfigs();
    }
    
    public Optional<SystemConfig> getConfigByKey(String configKey) {
        return systemConfigRepository.findByConfigKey(configKey);
    }
    
    public Optional<String> getConfigValue(String configKey) {
        return systemConfigRepository.getConfigValue(configKey);
    }
    
    public String getConfigValueOrDefault(String configKey, String defaultValue) {
        return getConfigValue(configKey).orElse(defaultValue);
    }
    
    public Long getConfigValueAsLong(String configKey, Long defaultValue) {
        return getConfigValue(configKey)
                .map(value -> {
                    try {
                        return Long.parseLong(value);
                    } catch (NumberFormatException e) {
                        return defaultValue;
                    }
                })
                .orElse(defaultValue);
    }
    
    public Boolean getConfigValueAsBoolean(String configKey, Boolean defaultValue) {
        return getConfigValue(configKey)
                .map(Boolean::parseBoolean)
                .orElse(defaultValue);
    }
    
    public SystemConfig createOrUpdateConfig(String configKey, String configValue, String dataType, String description, Boolean isPublic) {
        Optional<SystemConfig> existingConfig = systemConfigRepository.findByConfigKey(configKey);
        
        if (existingConfig.isPresent()) {
            SystemConfig config = existingConfig.get();
            config.setConfigValue(configValue);
            config.setDataType(dataType);
            config.setDescription(description);
            config.setIsPublic(isPublic);
            return systemConfigRepository.save(config);
        } else {
            SystemConfig newConfig = new SystemConfig();
            newConfig.setConfigKey(configKey);
            newConfig.setConfigValue(configValue);
            newConfig.setDataType(dataType);
            newConfig.setDescription(description);
            newConfig.setIsPublic(isPublic);
            return systemConfigRepository.save(newConfig);
        }
    }
    
    public SystemConfig updateConfig(String configKey, String configValue) {
        return systemConfigRepository.findByConfigKey(configKey)
            .map(config -> {
                config.setConfigValue(configValue);
                return systemConfigRepository.save(config);
            })
            .orElseThrow(() -> new RuntimeException("Configuration not found with key: " + configKey));
    }
    
    public void deleteConfig(String configKey) {
        systemConfigRepository.findByConfigKey(configKey)
            .ifPresentOrElse(
                systemConfigRepository::delete,
                () -> {
                    throw new RuntimeException("Configuration not found with key: " + configKey);
                }
            );
    }
    
    public List<SystemConfig> getConfigsByDataType(String dataType) {
        return systemConfigRepository.findByDataType(dataType);
    }
}
