package com.example.springdb2.auth.controller;

import com.example.springdb2.auth.dtos.AuthLoginRequest;
import com.example.springdb2.auth.dtos.AuthResponse;
import com.example.springdb2.auth.service.AuthService;
import com.example.springdb2.config.security.PermissionGroup;
import com.example.springdb2.config.security.UserPermission;
import com.example.springdb2.student.dtos.StudentCreateRequest;
import com.example.springdb2.student.dtos.StudentCreateResponse;
import com.example.springdb2.student.exceptions.StudentAlreadyExistsException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.EnumSet;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void registerReturnsCreatedStudentWithoutPassword() throws Exception {
        StudentCreateRequest request = new StudentCreateRequest("Vlad", "Breazu", "email@gmail.com", 22, "parola", 100, 50);
        StudentCreateResponse response = new StudentCreateResponse(
                1L, "Vlad", "Breazu", "email@gmail.com", 22, 100, 50,
                EnumSet.of(PermissionGroup.STUDENT_BASIC),
                Collections.emptySet(),
                EnumSet.of(UserPermission.STUDENT_READ, UserPermission.BOOK_READ, UserPermission.BOOK_WRITE),
                "jwt-token"
        );

        when(authService.register(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.permissionGroups").isArray())
                .andExpect(jsonPath("$.directPermissions").isArray())
                .andExpect(jsonPath("$.effectivePermissions").isArray())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void registerReturnsConflictWhenStudentExists() throws Exception {
        StudentCreateRequest request = new StudentCreateRequest("Vlad", "Breazu", "email@gmail.com", 22, "parola", 100, 50);
        when(authService.register(request)).thenThrow(new StudentAlreadyExistsException());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CONFLICT"));
    }

    @Test
    void loginReturnsTokenAndProfileWithoutPassword() throws Exception {
        AuthLoginRequest request = new AuthLoginRequest("email@gmail.com", "parola");
        AuthResponse response = new AuthResponse(
                1L, "Vlad", "Breazu", "email@gmail.com",
                EnumSet.of(PermissionGroup.STUDENT_BASIC),
                Collections.emptySet(),
                EnumSet.of(UserPermission.STUDENT_READ, UserPermission.BOOK_READ, UserPermission.BOOK_WRITE),
                "jwt-token"
        );
        when(authService.login(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("email@gmail.com"))
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.permissionGroups").isArray())
                .andExpect(jsonPath("$.directPermissions").isArray())
                .andExpect(jsonPath("$.effectivePermissions").isArray())
                .andExpect(jsonPath("$.password").doesNotExist());
    }
}
