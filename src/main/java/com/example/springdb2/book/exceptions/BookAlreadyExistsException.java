package com.example.springdb2.book.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BookAlreadyExistsException extends RuntimeException {
    public BookAlreadyExistsException() {
        super("BOOK_ALREADY_EXISTS_EXCEPTION");
    }
}
