package com.example.springdb2.book.controller;

import com.example.springdb2.book.dtos.BookCreateRequest;
import com.example.springdb2.book.dtos.BookPatchRequest;
import com.example.springdb2.book.dtos.BookPutRequest;
import com.example.springdb2.book.dtos.BookResponse;
import com.example.springdb2.book.exceptions.BookAlreadyExistsException;
import com.example.springdb2.book.exceptions.BookNotFoundException;
import com.example.springdb2.book.service.command.BookCommandService;
import com.example.springdb2.book.service.query.BookQueryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private BookQueryService bookQueryService;

    @MockitoBean
    private BookCommandService bookCommandService;

    @Test
    void getAllReturnsList() throws Exception {
        when(bookQueryService.getAllBooks()).thenReturn(List.of(
                new BookResponse(1, "Spring Boot in Action"),
                new BookResponse(2, "Clean Code")
        ));

        mockMvc.perform(get("/api/v1/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].name").value("Clean Code"));
    }

    @Test
    void patchReturnsUpdatedBook() throws Exception {
        BookPatchRequest request = new BookPatchRequest("Book");
        when(bookCommandService.patchBook(1L, request)).thenReturn(new BookResponse(1, "Book"));

        mockMvc.perform(patch("/api/v1/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Book"));

        verify(bookCommandService).patchBook(1L, request);
    }

    @Test
    void deleteReturnsNotFoundWhenServiceThrows() throws Exception {
        doThrow(new BookNotFoundException(1L)).when(bookCommandService).deleteBook(1L);

        mockMvc.perform(delete("/api/v1/books/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT FOUND"));
    }

    @Test
    void updateReturnsUpdatedResponse() throws Exception {
        BookPutRequest request = new BookPutRequest("Book");
        when(bookCommandService.updateBook(1L, request)).thenReturn(new BookResponse(1, "Book"));

        mockMvc.perform(put("/api/v1/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Book"));

        verify(bookCommandService).updateBook(1L, request);
    }

    @Test
    void createReturnsConflictWhenServiceThrowsAlreadyExists() throws Exception {
        BookCreateRequest request = new BookCreateRequest("Book");
        when(bookCommandService.createBook(eq(1L), any(BookCreateRequest.class))).thenThrow(new BookAlreadyExistsException());

        mockMvc.perform(post("/api/v1/students/1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("CONFLICT"));
    }

    @Test
    void createReturnsCreatedBook() throws Exception {
        BookCreateRequest request = new BookCreateRequest("Book");
        when(bookCommandService.createBook(eq(1L), any(BookCreateRequest.class))).thenReturn(new BookResponse(2, "Book"));

        mockMvc.perform(post("/api/v1/students/1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Book"));

        verify(bookCommandService).createBook(eq(1L), any(BookCreateRequest.class));
    }
}
