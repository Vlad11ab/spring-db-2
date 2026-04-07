package com.example.springdb2.book.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmptyBookUpdateRequestException extends RuntimeException {
    public EmptyBookUpdateRequestException() {
        super("EMPTY_BOOK_UPDATE_REQUEST_EXCEPTION");
    }
}
