package com.duongdat.filehub.repository;

import com.duongdat.filehub.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {
    
    Optional<SystemConfig> findByConfigKey(String configKey);
    
    @Query("SELECT sc FROM SystemConfig sc WHERE sc.isPublic = true")
    List<SystemConfig> findPublicConfigs();
    
    @Query("SELECT sc FROM SystemConfig sc WHERE sc.dataType = :dataType")
    List<SystemConfig> findByDataType(@Param("dataType") String dataType);
    
    @Query("SELECT sc.configValue FROM SystemConfig sc WHERE sc.configKey = :configKey")
    Optional<String> getConfigValue(@Param("configKey") String configKey);
    
    boolean existsByConfigKey(String configKey);
}
