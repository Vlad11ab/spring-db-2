package com.example.springdb2.book.controller;

import com.example.springdb2.book.dtos.BookCreateRequest;
import com.example.springdb2.book.dtos.BookPatchRequest;
import com.example.springdb2.book.dtos.BookPutRequest;
import com.example.springdb2.book.dtos.BookResponse;
import com.example.springdb2.book.service.command.BookCommandService;
import com.example.springdb2.book.service.query.BookQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Slf4j
@Tag(name = "Books", description = "Book management endpoints protected by fine-grained permissions")
public class BookController {

    private final BookCommandService bookCommandService;
    private final BookQueryService bookQueryService;

    public BookController(BookCommandService bookCommandService,BookQueryService bookQueryService){
        this.bookCommandService = bookCommandService;
        this.bookQueryService = bookQueryService;
    }

    @GetMapping("/books")
    @PreAuthorize("hasAuthority('book:read')")
    @Operation(
            summary = "List all books",
            description = "Requires permission book:read.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Books returned successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BookResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Missing permission book:read")
    })
    public ResponseEntity<List<BookResponse>> getAll(){
        log.info("HTTP GET /api/v1/books");
        return ResponseEntity.status(HttpStatus.OK).body(bookQueryService.getAllBooks());
    }

    @GetMapping("/books/{bookId}")
    @PreAuthorize("hasAuthority('book:read')")
    @Operation(
            summary = "Get a book by id",
            description = "Requires permission book:read.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book returned successfully",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Missing permission book:read"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long bookId){
        log.info("HTTP GET /api/v1/books/{}", bookId);
        return ResponseEntity.status(HttpStatus.OK).body(bookQueryService.getBookById(bookId));
    }

    @DeleteMapping("/books/{bookId}")
    @PreAuthorize("hasAuthority('book:edit')")
    @Operation(
            summary = "Delete a book",
            description = "Requires permission book:edit.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book deleted successfully",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Missing permission book:edit"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<BookResponse> delete(@PathVariable Long bookId){
        log.info("HTTP DELETE /api/v1/books/{}", bookId);
        return ResponseEntity.status(HttpStatus.OK).body(bookCommandService.deleteBook(bookId));
    }
    
    @PatchMapping("/books/{bookId}")
    @PreAuthorize("hasAuthority('book:edit')")
    @Operation(
            summary = "Patch a book",
            description = "Requires permission book:edit. Updates only the provided book fields.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Book patched successfully",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Missing permission book:edit"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<BookResponse> patch(@PathVariable Long bookId, @Valid @RequestBody BookPatchRequest patched){
        log.info("HTTP PATCH /api/v1/books/{}", bookId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(bookCommandService.patchBook(bookId,patched));
    }

    @PutMapping("/books/{bookId}")
    @PreAuthorize("hasAuthority('book:edit')")
    @Operation(
            summary = "Replace a book",
            description = "Requires permission book:edit.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Book updated successfully",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Missing permission book:edit"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "409", description = "Book already exists")
    })
    public ResponseEntity<BookResponse> update(@PathVariable Long bookId, @Valid @RequestBody BookPutRequest updated){
        log.info("HTTP PUT /api/v1/books/{}", bookId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(bookCommandService.updateBook(bookId,updated));
    }

    @PostMapping("/students/{studentId}/books")
    @PreAuthorize("hasAuthority('book:write')")
    @Operation(
            summary = "Create a book for a student",
            description = "Requires permission book:write.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Book created successfully",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Missing permission book:write"),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "409", description = "Book already exists")
    })
    public ResponseEntity<BookResponse> add(@PathVariable Long studentId, @Valid @RequestBody BookCreateRequest book){
        log.info("HTTP POST /api/v1/students/{}/books", studentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookCommandService.createBook(studentId, book));
    }
}
