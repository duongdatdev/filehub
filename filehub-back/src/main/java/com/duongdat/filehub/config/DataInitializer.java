package com.duongdat.filehub.config;

import com.duongdat.filehub.entity.Role;
import com.duongdat.filehub.entity.User;
import com.duongdat.filehub.repository.RoleRepository;
import com.duongdat.filehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Initialize roles
        if (roleRepository.findByName("USER").isEmpty()) {
            roleRepository.save(new Role("USER", "Regular user"));
        }
        
        if (roleRepository.findByName("ADMIN").isEmpty()) {
            roleRepository.save(new Role("ADMIN", "Administrator"));
        }

        // Create admin user if not exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
            User adminUser = new User(
                "admin",
                "admin@example.com",
                passwordEncoder.encode("admin123"),
                "Administrator",
                adminRole
            );
            userRepository.save(adminUser);
        }
    }
}
