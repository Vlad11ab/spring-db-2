package com.example.springdb2.book.service.command;

import com.example.springdb2.book.dtos.BookCreateRequest;
import com.example.springdb2.book.dtos.BookPatchRequest;
import com.example.springdb2.book.dtos.BookPutRequest;
import com.example.springdb2.book.dtos.BookResponse;

public interface BookCommandService {
    BookResponse createBook(Long studentId, BookCreateRequest req);
    BookResponse patchBook(Long bookId, BookPatchRequest req);
    BookResponse updateBook(Long bookId, BookPutRequest req);
    BookResponse deleteBook(Long bookId);
}
