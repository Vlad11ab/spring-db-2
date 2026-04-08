package com.example.springdb2.book.integration;

import com.example.springdb2.book.dtos.BookCreateRequest;
import com.example.springdb2.book.dtos.BookPatchRequest;
import com.example.springdb2.book.dtos.BookPutRequest;
import com.example.springdb2.book.dtos.BookResponse;
import com.example.springdb2.book.repository.BookRepository;
import com.example.springdb2.student.dtos.StudentCreateRequest;
import com.example.springdb2.student.dtos.StudentCreateResponse;
import com.example.springdb2.student.repository.StudentRepository;
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
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void cleanDatabase() {
        bookRepository.deleteAll();
        studentRepository.deleteAll();
    }

    @Test
    void createGetUpdatePatchDeleteFlow() throws Exception {
        StudentCreateResponse student = registerStudent("book-owner@example.com");

        BookCreateRequest createRequest = new BookCreateRequest("Book");
        MvcResult createResult = mockMvc.perform(post("/api/v1/students/{studentId}/books", student.id())
                        .with(jwt().authorities(new SimpleGrantedAuthority("book:write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        BookResponse created = objectMapper.readValue(createResult.getResponse().getContentAsByteArray(), BookResponse.class);

        mockMvc.perform(get("/api/v1/books/{bookId}", created.id())
                        .with(jwt().authorities(new SimpleGrantedAuthority("book:read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Book"));

        mockMvc.perform(put("/api/v1/books/{bookId}", created.id())
                        .with(jwt().authorities(new SimpleGrantedAuthority("book:edit")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BookPutRequest("UpdatedBook"))))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.name").value("UpdatedBook"));

        mockMvc.perform(patch("/api/v1/books/{bookId}", created.id())
                        .with(jwt().authorities(new SimpleGrantedAuthority("book:edit")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BookPatchRequest("PatchedBook"))))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.name").value("PatchedBook"));

        mockMvc.perform(delete("/api/v1/books/{bookId}", created.id())
                        .with(jwt().authorities(new SimpleGrantedAuthority("book:edit"))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/books/{bookId}", created.id())
                        .with(jwt().authorities(new SimpleGrantedAuthority("book:read"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void duplicateCreateReturnsConflict() throws Exception {
        StudentCreateResponse student = registerStudent("duplicate-book-owner@example.com");
        BookCreateRequest request = new BookCreateRequest("Book");

        mockMvc.perform(post("/api/v1/students/{studentId}/books", student.id())
                        .with(jwt().authorities(new SimpleGrantedAuthority("book:write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/students/{studentId}/books", student.id())
                        .with(jwt().authorities(new SimpleGrantedAuthority("book:write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void validationErrorsAreReturned() throws Exception {
        StudentCreateResponse student = registerStudent("validation-book-owner@example.com");
        BookCreateRequest invalid = new BookCreateRequest(null);

        mockMvc.perform(post("/api/v1/students/{studentId}/books", student.id())
                        .with(jwt().authorities(new SimpleGrantedAuthority("book:write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION FAILED"))
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.path").value("/api/v1/students/" + student.id() + "/books"));
    }

    @Test
    void getAllReturnsCreatedBooks() throws Exception {
        StudentCreateResponse student = registerStudent("all-books-owner@example.com");

        mockMvc.perform(post("/api/v1/students/{studentId}/books", student.id())
                        .with(jwt().authorities(new SimpleGrantedAuthority("book:write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BookCreateRequest("Book One"))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/students/{studentId}/books", student.id())
                        .with(jwt().authorities(new SimpleGrantedAuthority("book:write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BookCreateRequest("Book Two"))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/books")
                        .with(jwt().authorities(new SimpleGrantedAuthority("book:read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[1].id").exists());
    }

    private StudentCreateResponse registerStudent(String email) throws Exception {
        StudentCreateRequest request = new StudentCreateRequest("Vlad", "Breazu", email, 22, "parola", 100, 50);
        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsByteArray(), StudentCreateResponse.class);
    }
}
