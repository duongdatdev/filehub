package com.duongdat.filehub.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "gemini")
public class GeminiProperties {
    
    private Api api = new Api();
    private boolean enabled = true;
    private Model model = new Model();
    private long maxFileSize = 2147483648L; // 2GB (effectively no limit for Files API)
    private String supportedFormats = "txt,pdf,doc,docx,md,json,xml,csv,jpg,jpeg,png,gif,bmp,webp,mp4,mov,avi,mp3,wav,ogg";
    private int maxContentLength = 500000; // 500KB (increased for better analysis)
    private int maxOutputTokens = 8192; // Increased for more detailed responses
    private double temperature = 0.3; // AI creativity/randomness level
    
    public static class Api {
        private String key;
        
        public String getKey() {
            return key;
        }
        
        public void setKey(String key) {
            this.key = key;
        }
    }
    
    public static class Model {
        private String name = "gemini-2.0-flash";
        
        public String getName() {
            System.out.println("Using model: " + name);
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
    }
    
    // Getters and setters
    public Api getApi() {
        return api;
    }
    
    public void setApi(Api api) {
        this.api = api;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public Model getModel() {
        return model;
    }
    
    public void setModel(Model model) {
        this.model = model;
    }
    
    public long getMaxFileSize() {
        return maxFileSize;
    }
    
    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
    
    public String getSupportedFormats() {
        return supportedFormats;
    }
    
    public void setSupportedFormats(String supportedFormats) {
        this.supportedFormats = supportedFormats;
    }
    
    public List<String> getSupportedFormatsList() {
        return Arrays.asList(supportedFormats.split(","));
    }

    public int getMaxContentLength() {
        return maxContentLength;
    }

    public void setMaxContentLength(int maxContentLength) {
        this.maxContentLength = maxContentLength;
    }

    public int getMaxOutputTokens() {
        return maxOutputTokens;
    }

    public void setMaxOutputTokens(int maxOutputTokens) {
        this.maxOutputTokens = maxOutputTokens;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
