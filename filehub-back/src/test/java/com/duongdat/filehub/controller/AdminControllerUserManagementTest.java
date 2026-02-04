package com.duongdat.filehub.controller;

import com.duongdat.filehub.dto.request.BatchUserAssignmentRequest;
import com.duongdat.filehub.dto.request.UserAssignmentRequest;
import com.duongdat.filehub.dto.request.UpdateUserRoleRequest;
import com.duongdat.filehub.dto.response.AdminUserDetailResponse;
import com.duongdat.filehub.dto.response.UserResponse;
import com.duongdat.filehub.dto.response.UserDepartmentSummary;
import com.duongdat.filehub.dto.response.UserProjectSummary;
import com.duongdat.filehub.entity.Role;
import com.duongdat.filehub.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerUserManagementTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAssignUserToDepartment() throws Exception {
        UserAssignmentRequest request = new UserAssignmentRequest();
        request.setDepartmentId(1L);
        request.setRole("MEMBER");

        UserResponse userResponse = new UserResponse(1L, "testuser", "test@example.com", 
                "Test User", "USER", true, LocalDateTime.now());

        when(adminService.assignUserToDepartment(eq(1L), eq(1L))).thenReturn(userResponse);

        mockMvc.perform(post("/api/admin/users/1/department")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateUserDepartmentRole() throws Exception {
        UserResponse userResponse = new UserResponse(1L, "testuser", "test@example.com", 
                "Test User", "USER", true, LocalDateTime.now());

        when(adminService.updateUserDepartmentRole(eq(1L), eq(1L), eq("MANAGER")))
                .thenReturn(userResponse);

        mockMvc.perform(put("/api/admin/users/1/departments/1/role")
                .with(csrf())
                .param("role", "MANAGER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateUserRole() throws Exception {
        UserResponse userResponse = new UserResponse(1L, "testuser", "test@example.com", 
                "Test User", "ADMIN", true, LocalDateTime.now());

        when(adminService.updateUserRole(eq(1L), eq(Role.ADMIN)))
                .thenReturn(userResponse);

        UpdateUserRoleRequest request = new UpdateUserRoleRequest(Role.ADMIN);

        mockMvc.perform(patch("/api/admin/users/1/role")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUserDetailsWithAssignments() throws Exception {
        UserResponse userResponse = new UserResponse(1L, "testuser", "test@example.com", 
                "Test User", "USER", true, LocalDateTime.now());

        UserDepartmentSummary userDept = new UserDepartmentSummary();
        userDept.setId(1L);
        userDept.setName("Engineering");
        userDept.setRole("MEMBER");

        UserProjectSummary userProj = new UserProjectSummary();
        userProj.setId(1L);
        userProj.setName("Test Project");
        userProj.setRole("LEAD");

        List<UserDepartmentSummary> departments = Arrays.asList(userDept);
        List<UserProjectSummary> projects = Arrays.asList(userProj);

        AdminUserDetailResponse detailResponse = new AdminUserDetailResponse(
                userResponse, departments, projects);

        when(adminService.getUserDetailWithAssignments(eq(1L))).thenReturn(detailResponse);

        mockMvc.perform(get("/api/admin/users/1/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.departments").isArray())
                .andExpect(jsonPath("$.data.departments[0].role").value("MEMBER"))
                .andExpect(jsonPath("$.data.projects").isArray())
                .andExpect(jsonPath("$.data.projects[0].role").value("LEAD"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testBatchUpdateUserAssignments() throws Exception {
        BatchUserAssignmentRequest request = new BatchUserAssignmentRequest();
        request.setUserIds(Arrays.asList(1L, 2L));
        request.setDepartmentId(1L);
        request.setOperation("ADD");
        request.setRole("MEMBER");

        UserResponse user1 = new UserResponse(1L, "user1", "user1@example.com", 
                "User One", "USER", true, LocalDateTime.now());
        UserResponse user2 = new UserResponse(2L, "user2", "user2@example.com", 
                "User Two", "USER", true, LocalDateTime.now());

        when(adminService.batchUpdateUserAssignments(any(BatchUserAssignmentRequest.class)))
                .thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(post("/api/admin/users/batch-update")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testAdminEndpointRequiresAdminRole() throws Exception {
        mockMvc.perform(get("/api/admin/users/1/details"))
                .andExpect(status().isForbidden());
    }
}
