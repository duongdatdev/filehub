package com.duongdat.filehub.service;

import com.duongdat.filehub.entity.Role;
import com.duongdat.filehub.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "test@example.com", "password");
        testUser.setId(1L);
        testUser.setRole(Role.USER);
    }

    @Test
    void shouldGenerateValidAccessToken() {
        // Given
        String token = jwtService.generateTokenFromUser(testUser);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void shouldExtractUsernameFromToken() {
        // Given
        String token = jwtService.generateTokenFromUser(testUser);

        // When
        String extractedUsername = jwtService.extractUsername(token);

        // Then
        assertEquals(testUser.getUsername(), extractedUsername);
    }

    @Test
    void shouldExtractUserIdFromToken() {
        // Given
        String token = jwtService.generateTokenFromUser(testUser);

        // When
        Long extractedUserId = jwtService.getUserIdFromToken(token);

        // Then
        assertEquals(testUser.getId(), extractedUserId);
    }

    @Test
    void shouldExtractEmailFromToken() {
        // Given
        String token = jwtService.generateTokenFromUser(testUser);

        // When
        String extractedEmail = jwtService.getEmailFromToken(token);

        // Then
        assertEquals(testUser.getEmail(), extractedEmail);
    }

    @Test
    void shouldGenerateValidRefreshToken() {
        // Given
        String refreshToken = jwtService.generateRefreshTokenFromUser(testUser);

        // Then
        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
        assertTrue(jwtService.isTokenValid(refreshToken));
    }

    @Test
    void shouldReturnFalseForInvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertFalse(jwtService.isTokenValid(invalidToken));
    }
}
