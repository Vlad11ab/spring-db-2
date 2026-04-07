package com.example.springdb2.book.service.query.impl;

import com.example.springdb2.book.dtos.BookResponse;
import com.example.springdb2.book.exceptions.BookNotFoundException;
import com.example.springdb2.book.mappers.BookMapper;
import com.example.springdb2.book.model.Book;
import com.example.springdb2.book.repository.BookRepository;
import com.example.springdb2.book.service.query.BookQueryService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class BookQueryServiceImpl implements BookQueryService {

    private BookRepository bookRepository;

    public BookQueryServiceImpl(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(BookMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse getBookById(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        return BookMapper.toDto(book);

    }
}
