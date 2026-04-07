package com.example.springdb2.book.controller;

import com.example.springdb2.book.dtos.BookCreateRequest;
import com.example.springdb2.book.dtos.BookPatchRequest;
import com.example.springdb2.book.dtos.BookPutRequest;
import com.example.springdb2.book.dtos.BookResponse;
import com.example.springdb2.book.exceptions.BookAlreadyExistsException;
import com.example.springdb2.book.exceptions.BookNotFoundException;
import com.example.springdb2.book.model.Book;
import com.example.springdb2.book.service.command.BookCommandService;
import com.example.springdb2.book.service.query.BookQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookQueryService bookQueryService;

    @MockitoBean
    private BookCommandService bookCommandService;

    @Autowired
    private ObjectMapper objectMapper;

    Book book1 = Book.builder().id(1).name("Spring Boot in Action").build();
    Book book2 = Book.builder().id(2).name("Clean Code").build();
    Book book3 = Book.builder().id(3).name("The Pragmatic Programmer").build();
    BookResponse response1 = new BookResponse(1, "Spring Boot in Action");
    BookResponse response2 = new BookResponse(2, "Clean Code");
    BookResponse response3 = new BookResponse(3, "The Pragmatic Programmer");

    @Test
    void getAllReturnsList() throws Exception {
        when(bookQueryService.getAllBooks()).thenReturn(List.of(response1, response2, response3));

        MvcResult result = mockMvc.perform(get("/api/v1/books-students/books/all"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void patchReturnsOk() throws Exception {
        BookPatchRequest patch = new BookPatchRequest("Book");
        BookResponse bookResponse = new BookResponse(1, "Book");

        when(bookCommandService.patchBook(1L, patch)).thenReturn(bookResponse);

        mockMvc.perform(patch("/api/v1/books-students/books/edit/patch/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Book"));

        verify(bookCommandService).patchBook(1L, patch);
    }

    @Test
    void deleteReturnsOk() throws Exception {
        mockMvc.perform(delete("/api/v1/books-students/books/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBookReturnsConflictWhenServiceThrowsNotFound() throws Exception {
        doThrow(new BookNotFoundException(1L)).when(bookCommandService).deleteBook(1L);

        mockMvc.perform(delete("/api/v1/books-students/books/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT FOUND"))
                .andExpect(jsonPath("$.message").value("BOOK_NOT_FOUND_EXCEPTION"));
    }

    @Test
    void updateReturnsUpdatedResponse() throws Exception {
        BookPutRequest request = new BookPutRequest("Book");
        BookResponse bookResponse = new BookResponse(1, "Book");

        when(bookCommandService.updateBook(1L, request)).thenReturn(bookResponse);

        mockMvc.perform(put("/api/v1/books-students/books/edit/update/1")
                        .contentType(String.valueOf(String.valueOf(MediaType.APPLICATION_JSON)))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentTypeCompatibleWith(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Book"));

        verify(bookCommandService).updateBook(1L, request);
    }

    @Test
    void createBookReturnsConflictWhenServiceThrowsAlreadyExists() throws Exception {
        BookCreateRequest request = new BookCreateRequest("Book");

        when(bookCommandService.createBook(eq(1L), any(BookCreateRequest.class))).thenThrow(new BookAlreadyExistsException());

        mockMvc.perform(post("/api/v1/books-students/books/add/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("BOOK_ALREADY_EXISTS_EXCEPTION"));
    }

    @Test
    void createReturns201() throws Exception {
        BookCreateRequest request = new BookCreateRequest("Book");
        BookResponse bookResponse = new BookResponse(2, "Book");

        when(bookCommandService.createBook(eq(1L), any(BookCreateRequest.class))).thenReturn(bookResponse);

        mockMvc.perform(post("/api/v1/books-students/books/add/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Book"));

        verify(bookCommandService).createBook(eq(1L), any(BookCreateRequest.class));
    }
}
