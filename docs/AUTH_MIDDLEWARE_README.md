# Authentication & Authorization Middleware

This document describes the authentication and authorization middleware implementation for the FileHub backend application.

## Overview

The middleware system provides comprehensive security features including:

- JWT-based authentication
- Role-based authorization
- Middleware components for filtering and validating requests
- Security utilities for easy access to current user information

## Components

### 1. JWT Service (`JwtService.java`)

Handles JWT token generation, validation, and extraction of user information.

**Key Features:**
- Generate access and refresh tokens
- Extract user information from tokens
- Validate token authenticity and expiration
- Configurable token expiration times

**Configuration:**
```properties
app.jwt.secret=YOUR_SECRET_KEY
app.jwt.expiration=86400000        # 24 hours
app.jwt.refresh-expiration=604800000 # 7 days
```

### 2. JWT Authentication Filter (`JwtAuthenticationFilter.java`)

Spring Security filter that intercepts requests and validates JWT tokens.

**Features:**
- Extracts JWT tokens from Authorization header
- Validates tokens and sets authentication context
- Adds user information to request attributes
- Handles authentication failures gracefully

### 3. Authorization Interceptor (`AuthorizationInterceptor.java`)

Web interceptor that enforces role-based access control using annotations.

**Features:**
- Processes `@RequireRole` annotations
- Supports multiple role requirements
- Returns appropriate HTTP status codes (401, 403)
- Logs access attempts and failures

### 4. Security Configuration (`SecurityConfig.java`)

Central security configuration that integrates all components.

**Features:**
- Configures JWT authentication filter
- Sets up CORS policy
- Defines protected and public endpoints
- Integrates authentication providers

### 5. User Details Service (`UserDetailsServiceImpl.java`)

Spring Security service that loads user details for authentication.

**Features:**
- Loads users by username or ID
- Maps user roles to Spring Security authorities
- Handles user status (active/inactive)

### 6. Security Utility (`SecurityUtil.java`)

Helper class for accessing current user information in controllers and services.

**Features:**
- Get current authenticated user
- Check user roles and permissions
- Utility methods for common security operations

## Usage

### Authentication

#### Register a New User
```bash
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "securePassword",
  "fullName": "John Doe"
}
```

#### User Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "securePassword"
}
```

**Response includes:**
- User information
- Access token (for API requests)
- Refresh token (for token renewal)

#### Refresh Token
```bash
POST /api/auth/refresh
Authorization: Bearer <refresh_token>
```

#### Logout
```bash
POST /api/auth/logout
Authorization: Bearer <access_token>
```

### Authorization

#### Using Role-based Annotations

```java
@RestController
@RequestMapping("/api/admin")
@RequireRole("ADMIN")  // All endpoints require ADMIN role
public class AdminController {

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        // Only accessible by ADMIN users
    }
}

@RestController
public class UserController {

    @GetMapping("/dashboard")
    @RequireRole({"USER", "ADMIN"})  // Multiple roles allowed
    public ResponseEntity<?> getDashboard() {
        // Accessible by USER or ADMIN
    }

    @PostMapping("/settings")
    @RequireRole(value = {"USER", "ADMIN"}, requireAll = false)
    public ResponseEntity<?> updateSettings() {
        // User needs at least one of the specified roles
    }
}
```

#### Using Security Utility

```java
@RestController
public class ProfileController {

    private final SecurityUtil securityUtil;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        Optional<User> currentUser = securityUtil.getCurrentUser();
        String role = securityUtil.getCurrentUserRole();
        
        if (securityUtil.hasRole("ADMIN")) {
            // Admin-specific logic
        }
        
        return ResponseEntity.ok(currentUser);
    }
}
```

### Making Authenticated Requests

Include the JWT token in the Authorization header:

```bash
GET /api/user/profile
Authorization: Bearer <access_token>
```

## Security Features

### Token Security
- Tokens are signed using HMAC-SHA256
- Configurable expiration times
- Refresh token mechanism to maintain sessions
- Token validation on every request

### Role-based Access Control
- Flexible role assignment
- Method-level authorization
- Class-level authorization
- Support for multiple role requirements

### Error Handling
- Proper HTTP status codes (401, 403)
- Detailed error messages
- Request logging for security monitoring

### CORS Configuration
- Configurable allowed origins
- Support for credentials
- Proper preflight handling

## Example Endpoints

### Public Endpoints (No Authentication Required)
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `GET /api/public/**` - Public resources

### Protected Endpoints (Authentication Required)
- `GET /api/user/profile` - Get current user profile
- `POST /api/user/settings` - Update user settings
- `POST /api/auth/refresh` - Refresh tokens
- `POST /api/auth/logout` - User logout

### Admin-only Endpoints (ADMIN Role Required)
- `GET /api/admin/users` - List all users
- `GET /api/admin/stats` - System statistics
- `PUT /api/admin/users/{id}/toggle-status` - Toggle user status
- `DELETE /api/admin/users/{id}` - Delete user

## Error Responses

### 401 Unauthorized
```json
{
  "error": "Unauthorized",
  "message": "Authentication required to access this resource",
  "status": 401,
  "timestamp": 1706789123456,
  "path": "/api/user/profile"
}
```

### 403 Forbidden
```json
{
  "error": "Forbidden",
  "message": "Insufficient permissions",
  "status": 403
}
```

## Configuration

### Application Properties
```properties
# JWT Configuration
app.jwt.secret=YOUR_SECRET_KEY_HERE
app.jwt.expiration=86400000        # 24 hours in milliseconds
app.jwt.refresh-expiration=604800000 # 7 days in milliseconds

# Database Configuration (for user storage)
spring.datasource.url=jdbc:mysql://localhost:3306/filehub_db
spring.datasource.username=root
spring.datasource.password=secret

# Security Logging
logging.level.com.duongdat.filehub.config=DEBUG
```

### Dependencies
```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
    implementation 'io.jsonwebtoken:jjwt-impl:0.12.3'
    implementation 'io.jsonwebtoken:jjwt-jackson:0.12.3'
}
```

## Best Practices

1. **Token Storage**: Store JWT tokens securely on the client side (preferably in httpOnly cookies)
2. **Token Rotation**: Implement refresh token rotation for enhanced security
3. **Rate Limiting**: Consider implementing rate limiting for authentication endpoints
4. **Audit Logging**: Log all authentication and authorization events
5. **Password Security**: Use strong password policies and consider implementing password reset functionality
6. **HTTPS**: Always use HTTPS in production to protect tokens in transit

This middleware system provides a robust foundation for securing your Spring Boot application with industry-standard practices.
