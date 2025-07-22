package com.duongdat.filehub.config;

import com.duongdat.filehub.annotation.RequireRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, 
                           @NonNull HttpServletResponse response, 
                           @NonNull Object handler) throws Exception {
        
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        
        if (requireRole == null) {
            requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }
        
        if (requireRole == null) {
            return true; // No role requirement
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return handleUnauthorized(response, "User not authenticated");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String[] requiredRoles = requireRole.value();
        boolean requireAll = requireRole.requireAll();

        if (requiredRoles.length == 0) {
            return true; // No specific roles required
        }

        boolean hasRequiredRole = checkUserRoles(userDetails, requiredRoles, requireAll);
        
        if (!hasRequiredRole) {
            String userRole = extractUserRole(userDetails);
            log.warn("Access denied for user: {} with role: {} to endpoint: {}", 
                    userDetails.getUsername(), userRole, request.getRequestURI());
            return handleForbidden(response, "Insufficient permissions");
        }

        return true;
    }

    private boolean checkUserRoles(UserDetails userDetails, String[] requiredRoles, boolean requireAll) {
        if (requireAll) {
            // User must have ALL required roles
            return Arrays.stream(requiredRoles)
                    .allMatch(role -> userDetails.getAuthorities().stream()
                            .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role)));
        } else {
            // User must have AT LEAST ONE of the required roles
            return Arrays.stream(requiredRoles)
                    .anyMatch(role -> userDetails.getAuthorities().stream()
                            .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role)));
        }
    }

    private String extractUserRole(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .orElse("UNKNOWN");
    }

    private boolean handleUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", message);
        errorResponse.put("status", 401);
        
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        return false;
    }

    private boolean handleForbidden(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Forbidden");
        errorResponse.put("message", message);
        errorResponse.put("status", 403);
        
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        return false;
    }
}
