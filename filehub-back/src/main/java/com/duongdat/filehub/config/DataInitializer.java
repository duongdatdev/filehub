package com.duongdat.filehub.config;

import com.duongdat.filehub.entity.User;
import com.duongdat.filehub.entity.Role;
import com.duongdat.filehub.entity.FileCategory;
import com.duongdat.filehub.repository.UserRepository;
import com.duongdat.filehub.repository.FileCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FileCategoryRepository fileCategoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create file categories if not exists
        if (fileCategoryRepository.count() == 0) {
            createFileCategory("Documents", "Text documents and PDFs", "#2196F3", "document", 1);
            createFileCategory("Images", "Photos and graphics", "#4CAF50", "image", 2);
            createFileCategory("Videos", "Video files and multimedia", "#FF9800", "video", 3);
            createFileCategory("Audio", "Music and audio files", "#9C27B0", "audio", 4);
            createFileCategory("Archives", "Compressed files and archives", "#607D8B", "archive", 5);
            createFileCategory("Code", "Source code and scripts", "#FF5722", "code", 6);
            createFileCategory("Others", "Other file types", "#9E9E9E", "file", 7);
            System.out.println("File categories created");
        }
        
        // Create admin user if not exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@filehub.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("System Administrator");
            admin.setRole(Role.ADMIN);
            admin.setActive(true);
            userRepository.save(admin);
            System.out.println("Admin user created: username=admin, password=admin123");
        }

        // Create some test users if database is empty
        if (userRepository.count() < 5) {
            for (int i = 1; i <= 10; i++) {
                if (!userRepository.existsByUsername("user" + i)) {
                    User user = new User();
                    user.setUsername("user" + i);
                    user.setEmail("user" + i + "@example.com");
                    user.setPassword(passwordEncoder.encode("password123"));
                    user.setFullName("Test User " + i);
                    user.setRole(Role.USER);
                    user.setActive(i % 4 != 0); // Some inactive users
                    userRepository.save(user);
                }
            }
            System.out.println("Test users created (user1-user10)");
        }
    }
    
    private void createFileCategory(String name, String description, String color, String icon, int displayOrder) {
        FileCategory category = new FileCategory();
        category.setName(name);
        category.setDescription(description);
        category.setColor(color);
        category.setIcon(icon);
        category.setDisplayOrder(displayOrder);
        category.setIsActive(true);
        fileCategoryRepository.save(category);
    }
}
