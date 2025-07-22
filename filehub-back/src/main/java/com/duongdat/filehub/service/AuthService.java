package com.duongdat.filehub.service;

import com.duongdat.filehub.dto.request.LoginRequest;
import com.duongdat.filehub.dto.request.RegisterRequest;
import com.duongdat.filehub.dto.response.UserResponse;
import com.duongdat.filehub.entity.Role;
import com.duongdat.filehub.entity.User;
import com.duongdat.filehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create new user
        User user = new User(
            request.getUsername(),
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            request.getFullName()
        );
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);
        
        // Generate JWT token
        String accessToken = jwtService.generateTokenFromUser(savedUser);
        String refreshToken = jwtService.generateRefreshTokenFromUser(savedUser);
        
        return mapToUserResponse(savedUser, accessToken, refreshToken);
    }

    public UserResponse login(LoginRequest request) {
        // Authenticate user
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isActive()) {
            throw new RuntimeException("Account is disabled");
        }

        // Generate JWT tokens
        String accessToken = jwtService.generateTokenFromUser(user);
        String refreshToken = jwtService.generateRefreshTokenFromUser(user);

        return mapToUserResponse(user, accessToken, refreshToken);
    }

    public Map<String, String> refreshToken(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isActive()) {
            throw new RuntimeException("Account is disabled");
        }

        String newAccessToken = jwtService.generateTokenFromUser(user);
        String newRefreshToken = jwtService.generateRefreshTokenFromUser(user);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);

        return tokens;
    }

    public void logout() {
        // In a token-based authentication system, you would:
        // 1. Invalidate the token on the server side
        // 2. Add the token to a blacklist
        // 3. Clear any server-side session data
        
        // For now, since we're using a simple setup, we'll just return successfully
        // The actual token cleanup is handled on the frontend
        
        // In a more advanced implementation, you might:
        // - Save logout timestamp to user record
        // - Invalidate refresh tokens
        // - Clear Redis sessions, etc.
    }

    private UserResponse mapToUserResponse(User user, String accessToken, String refreshToken) {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFullName() != null ? user.getFullName() : user.getUsername(),
            user.getRole().toString(),
            user.isActive(),
            user.getCreatedAt(),
            accessToken,
            refreshToken
        );
    }
}
