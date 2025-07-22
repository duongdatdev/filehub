package com.duongdat.filehub.service;

import com.duongdat.filehub.dto.request.AdminUserFilterRequest;
import com.duongdat.filehub.dto.response.PageResponse;
import com.duongdat.filehub.dto.response.UserResponse;
import com.duongdat.filehub.entity.User;
import com.duongdat.filehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public PageResponse<UserResponse> getAllUsers(AdminUserFilterRequest request) {
        // Create sort
        Sort.Direction direction = request.getSortDirection().equalsIgnoreCase("DESC") 
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, request.getSortBy());
        
        // Create pageable
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        
        // Get users with filters
        Page<User> userPage = userRepository.findUsersWithFilters(
            request.getUsername(),
            request.getEmail(),
            request.getRole(),
            request.getIsActive(),
            pageable
        );
        
        // Convert to UserResponse DTOs
        List<UserResponse> userResponses = userPage.getContent().stream()
            .map(this::mapToUserResponse)
            .toList();
        
        // Create PageResponse
        return new PageResponse<>(
            userResponses,
            userPage.getNumber(),
            userPage.getSize(),
            userPage.getTotalElements(),
            userPage.getTotalPages(),
            userPage.isFirst(),
            userPage.isLast(),
            userPage.hasNext(),
            userPage.hasPrevious()
        );
    }

    public Optional<UserResponse> getUserById(Long id) {
        return userRepository.findById(id)
            .map(this::mapToUserResponse);
    }

    public UserResponse updateUserStatus(Long id, boolean isActive) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setActive(isActive);
        User savedUser = userRepository.save(user);
        
        return mapToUserResponse(savedUser);
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFullName() != null ? user.getFullName() : user.getUsername(),
            user.getRole().toString(),
            user.isActive(),
            user.getCreatedAt()
        );
    }
}
