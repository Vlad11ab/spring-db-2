package com.example.springdb2.book.controller;

import com.example.springdb2.book.dtos.BookCreateRequest;
import com.example.springdb2.book.dtos.BookPatchRequest;
import com.example.springdb2.book.dtos.BookPutRequest;
import com.example.springdb2.book.dtos.BookResponse;
import com.example.springdb2.book.service.command.BookCommandService;
import com.example.springdb2.book.service.query.BookQueryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books-students")
@Slf4j
public class BookController {

    private BookCommandService bookCommandService;
    private BookQueryService bookQueryService;

    public BookController(BookCommandService bookCommandService,BookQueryService bookQueryService){
        this.bookCommandService = bookCommandService;
        this.bookQueryService = bookQueryService;
    }

    @GetMapping("/books/all")
    public ResponseEntity<List<BookResponse>> getAll(){
        log.info("HTTP GET /api/v1/books-students/books/all");
        return ResponseEntity.status(HttpStatus.OK).body(bookQueryService.getAllBooks());
    }

    @GetMapping("/books/get/{bookId}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long bookId){
        log.info("HTTP GET /api/v1/books/{}", bookId);
        return ResponseEntity.status(HttpStatus.OK).body(bookQueryService.getBookById(bookId));
    }

    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<BookResponse> delete(@PathVariable Long bookId){
        log.info("HTTP DELETE /api/v1/books-students/books/{}", bookId);
        return ResponseEntity.status(HttpStatus.OK).body(bookCommandService.deleteBook(bookId));
    }
    
    @PatchMapping("/books/edit/patch/{bookId}")
    public ResponseEntity<BookResponse> patch(@PathVariable Long bookId, @Valid @RequestBody BookPatchRequest patched){
        log.info("HTTP PATCH /api/v1/books-students/books/edit/patch/{}", bookId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(bookCommandService.patchBook(bookId,patched));
    }

    @PutMapping("/books/edit/update/{bookId}")
    public ResponseEntity<BookResponse> update(@PathVariable Long bookId, @Valid @RequestBody BookPutRequest updated){
        log.info("HTTP PUT /api/v1/books-students/books/edit/update/{}", bookId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(bookCommandService.updateBook(bookId,updated));
    }

    @PostMapping("/books/add/{studentId}")
    public ResponseEntity<BookResponse> add(@PathVariable Long studentId, @Valid @RequestBody BookCreateRequest book){
        log.info("HTTP POST /api/v1/books-students/books/add{}", studentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookCommandService.createBook(studentId, book));
    }

    

}
