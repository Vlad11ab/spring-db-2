package com.example.springdb2.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EndpointAuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void unauthenticatedRequestIsRejected() throws Exception {
        mockMvc.perform(get("/api/v1/books"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void bookReadAuthorityCanAccessBookReads() throws Exception {
        mockMvc.perform(get("/api/v1/books")
                        .with(jwt().authorities(new SimpleGrantedAuthority("book:read"))))
                .andExpect(status().isOk());
    }

    @Test
    void missingBookEditAuthorityIsForbidden() throws Exception {
        mockMvc.perform(patch("/api/v1/books/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("book:write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void bookWriteAuthorityCanCreateBooks() throws Exception {
        mockMvc.perform(post("/api/v1/students/1/books")
                        .with(jwt().authorities(new SimpleGrantedAuthority("book:write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Book\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void studentReadAuthorityCanAccessStudentReads() throws Exception {
        mockMvc.perform(get("/api/v1/students/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("student:read"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void missingStudentEditAuthorityIsForbidden() throws Exception {
        mockMvc.perform(patch("/api/v1/students/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("student:read")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void studentEditAuthorityCanReachStudentWriteEndpoints() throws Exception {
        mockMvc.perform(patch("/api/v1/students/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("student:edit")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void studentEditAuthorityCannotManagePermissions() throws Exception {
        mockMvc.perform(put("/api/v1/students/1/permissions")
                        .with(jwt().authorities(new SimpleGrantedAuthority("student:edit")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"permissionGroups\":[\"STUDENT_BASIC\"],\"directPermissions\":[]}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void permissionsEditAuthorityCanManagePermissions() throws Exception {
        mockMvc.perform(put("/api/v1/students/1/permissions")
                        .with(jwt().authorities(new SimpleGrantedAuthority("student:permissions:edit")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"permissionGroups\":[\"STUDENT_BASIC\"],\"directPermissions\":[]}"))
                .andExpect(status().isNotFound());
    }
}
