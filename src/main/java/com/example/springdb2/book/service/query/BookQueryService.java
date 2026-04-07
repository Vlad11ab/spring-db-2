package com.example.springdb2.book.service.query;

import com.example.springdb2.book.dtos.BookResponse;

import java.util.List;

public interface BookQueryService {
    List<BookResponse> getAllBooks();
    BookResponse getBookById(Long bookId);

}
