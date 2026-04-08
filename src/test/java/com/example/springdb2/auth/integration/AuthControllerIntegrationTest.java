package com.example.springdb2.auth.integration;

import com.example.springdb2.auth.dtos.AuthLoginRequest;
import com.example.springdb2.student.dtos.StudentCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.example.springdb2.student.repository.StudentRepository studentRepository;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void cleanDatabase() {
        studentRepository.deleteAll();
    }

    @Test
    void registerCreatesStudentWithDefaultPermissionGroup() throws Exception {
        StudentCreateRequest request = new StudentCreateRequest("Vlad", "Breazu", "vlad@gmail.com", 22, "parola", 50, 10);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("vlad@gmail.com"))
                .andExpect(jsonPath("$.permissionGroups[0]").value("STUDENT_BASIC"))
                .andExpect(jsonPath("$.effectivePermissions").isArray())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void loginReturnsJwtAndPermissions() throws Exception {
        StudentCreateRequest request = new StudentCreateRequest("Vlad", "Breazu", "vlad@gmail.com", 22, "parola", 50, 10);
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AuthLoginRequest("vlad@gmail.com", "parola"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("vlad@gmail.com"))
                .andExpect(jsonPath("$.permissionGroups[0]").value("STUDENT_BASIC"))
                .andExpect(jsonPath("$.effectivePermissions").isArray())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void loginReturnsUnauthorizedForInvalidCredentials() throws Exception {
        StudentCreateRequest request = new StudentCreateRequest("Vlad", "Breazu", "vlad@gmail.com", 22, "parola", 50, 10);
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AuthLoginRequest("vlad@gmail.com", "gresita"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }
}
