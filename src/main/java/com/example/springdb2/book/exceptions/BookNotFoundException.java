package com.example.springdb2.book.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Long bookId) {
        super("BOOK_NOT_FOUND_EXCEPTION");
    }
}
