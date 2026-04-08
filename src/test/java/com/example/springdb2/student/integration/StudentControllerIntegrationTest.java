package com.example.springdb2.student.integration;

import com.example.springdb2.config.security.PermissionGroup;
import com.example.springdb2.config.security.UserPermission;
import com.example.springdb2.auth.dtos.AuthLoginRequest;
import com.example.springdb2.student.dtos.StudentCreateRequest;
import com.example.springdb2.student.dtos.StudentCreateResponse;
import com.example.springdb2.student.dtos.StudentPatchRequest;
import com.example.springdb2.student.dtos.StudentPermissionsUpdateRequest;
import com.example.springdb2.student.dtos.StudentPutRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.EnumSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private com.example.springdb2.student.repository.StudentRepository studentRepository;

    @BeforeEach
    void cleanDatabase() {
        studentRepository.deleteAll();
    }

    @Test
    void createGetUpdatePatchDeleteFlow() throws Exception {
        StudentCreateRequest createRequest = new StudentCreateRequest("Vlad", "Breazu", "vlad@gmail.com", 22, "parola", 50, 100);

        MvcResult createResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andReturn();

        StudentCreateResponse created = objectMapper.readValue(createResult.getResponse().getContentAsByteArray(), StudentCreateResponse.class);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AuthLoginRequest("vlad@gmail.com", "parola"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.password").doesNotExist());

        mockMvc.perform(get("/api/v1/students/{studentId}", created.id())
                        .with(jwt().authorities(new SimpleGrantedAuthority("student:read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Vlad"))
                .andExpect(jsonPath("$.lastName").value("Breazu"))
                .andExpect(jsonPath("$.email").value("vlad@gmail.com"))
                .andExpect(jsonPath("$.age").value(22))
                .andExpect(jsonPath("$.password").doesNotExist());

        mockMvc.perform(put("/api/v1/students/{studentId}", created.id())
                        .with(jwt().authorities(new SimpleGrantedAuthority("student:edit")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new StudentPutRequest("Alex", "Stefan", "alex@gmail.com", 21, "parolaNoua", 30, 50))))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.firstName").value("Alex"))
                .andExpect(jsonPath("$.lastName").value("Stefan"))
                .andExpect(jsonPath("$.email").value("alex@gmail.com"))
                .andExpect(jsonPath("$.age").value(21))
                .andExpect(jsonPath("$.password").doesNotExist());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AuthLoginRequest("alex@gmail.com", "parolaNoua"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());

        mockMvc.perform(patch("/api/v1/students/{studentId}", created.id())
                        .with(jwt().authorities(new SimpleGrantedAuthority("student:edit")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new StudentPatchRequest("andrei@gmail.com", "parolaparola", 90))))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.email").value("andrei@gmail.com"))
                .andExpect(jsonPath("$.nrCrediteEfectuate").value(90))
                .andExpect(jsonPath("$.password").doesNotExist());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AuthLoginRequest("andrei@gmail.com", "parolaparola"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());

        mockMvc.perform(delete("/api/v1/students/{studentId}", created.id())
                        .with(jwt().authorities(new SimpleGrantedAuthority("student:edit"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").doesNotExist());

        mockMvc.perform(get("/api/v1/students/{studentId}", created.id())
                        .with(jwt().authorities(new SimpleGrantedAuthority("student:read"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void duplicateCreateReturnsConflict() throws Exception {
        StudentCreateRequest request = new StudentCreateRequest("Vlad", "Breazu", "vlad@gmail.com", 22, "parola", 50, 10);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void validationErrorsAreReturned() throws Exception {
        StudentCreateRequest invalid = new StudentCreateRequest(null, "Breazu", "vlad@gmail.com", 22, "parola", 50, 10);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION FAILED"))
                .andExpect(jsonPath("$.fieldErrors.firstName").exists())
                .andExpect(jsonPath("$.path").value("/api/v1/auth/register"));
    }

    @Test
    void getAllReturnsRegisteredStudents() throws Exception {
        registerStudent("vlad1@gmail.com");
        registerStudent("vlad2@gmail.com");

        mockMvc.perform(get("/api/v1/students")
                        .with(jwt().authorities(new SimpleGrantedAuthority("student:read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[1].id").exists());
    }

    @Test
    void updatePermissionsReplacesGroupsAndDirectPermissions() throws Exception {
        StudentCreateResponse created = registerStudent("permissions@gmail.com");
        StudentPermissionsUpdateRequest request = new StudentPermissionsUpdateRequest(
                EnumSet.of(PermissionGroup.STUDENT_BASIC, PermissionGroup.BOOK_EDITOR),
                EnumSet.of(UserPermission.STUDENT_PERMISSIONS_EDIT)
        );

        mockMvc.perform(put("/api/v1/students/{studentId}/permissions", created.id())
                        .with(jwt().authorities(new SimpleGrantedAuthority("student:permissions:edit")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.permissionGroups").isArray())
                .andExpect(jsonPath("$.directPermissions").isArray())
                .andExpect(jsonPath("$.effectivePermissions").isArray());
    }

    private StudentCreateResponse registerStudent(String email) throws Exception {
        StudentCreateRequest request = new StudentCreateRequest("Vlad", "Breazu", email, 22, "parola", 50, 10);
        MvcResult createResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(createResult.getResponse().getContentAsByteArray(), StudentCreateResponse.class);
    }
}
