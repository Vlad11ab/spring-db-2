package com.example.springdb2.book.integration;

import com.example.springdb2.book.dtos.BookCreateRequest;
import com.example.springdb2.book.dtos.BookPatchRequest;
import com.example.springdb2.book.dtos.BookPutRequest;
import com.example.springdb2.book.dtos.BookResponse;
import com.example.springdb2.book.repository.BookRepository;
import com.example.springdb2.student.dtos.StudentPatchRequest;
import com.example.springdb2.student.dtos.StudentPutRequest;
import com.example.springdb2.student.dtos.StudentResponse;
import com.example.springdb2.student.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void cleanDatabase(){
        bookRepository.deleteAll();
    }
    @Test
    void createGetUpdatePatchDeleteFlow() throws Exception{
        Student student = Student.builder().id(1L).firstName("Vlad").lastName("Breazu").build();

        BookCreateRequest createRequest = new BookCreateRequest(
                "Book"
        );

        MvcResult createResult = mockMvc.perform(post("/api/v1/books-students/books/add/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();
        BookResponse created = objectMapper.readValue(createResult.getResponse().getContentAsByteArray(), BookResponse.class);

        mockMvc.perform(get("/api/v1/books-students/books/get/{bookId}", created.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Book"));

        BookPutRequest put = new BookPutRequest("UpdatedBook");
        mockMvc.perform(put("/api/v1/books-students/books/edit/update/{bookId}",created.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(put)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.name").value("UpdatedBook"));

        BookPatchRequest patch = new BookPatchRequest("PatchedBook");
        mockMvc.perform(patch("/api/v1/books-students/books/edit/patch/{bookId}",created.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.name").value("PatchedBook"));

        mockMvc.perform(delete("/api/v1/books-students/books/{bookId}", created.id()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/books-students/books/get/{bookId}",created.id()))
                .andExpect(status().isNotFound());

    }

    @Test
    void duplicateCreateReturnsConflict() throws Exception {
        Student student = Student.builder().id(1L).firstName("Vlad").lastName("Breazu").build();
        BookCreateRequest request = new BookCreateRequest("Book");

        mockMvc.perform(post("/api/v1/books-students/books/add/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated()).andReturn();


        mockMvc.perform(post("/api/v1/books-students/books/add/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void validationErrorAreReturned() throws Exception{
        Student student = Student.builder().id(1L).firstName("Vlad").lastName("Breazu").build();
        BookCreateRequest invalid = new BookCreateRequest(null);

        mockMvc.perform(post("/api/v1/books-students/books/add/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }






}
