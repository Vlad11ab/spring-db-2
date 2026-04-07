package com.example.springdb2.book.service.command.impl;

import com.example.springdb2.book.dtos.BookCreateRequest;
import com.example.springdb2.book.dtos.BookPatchRequest;
import com.example.springdb2.book.dtos.BookPutRequest;
import com.example.springdb2.book.dtos.BookResponse;
import com.example.springdb2.book.exceptions.BookAlreadyExistsException;
import com.example.springdb2.book.exceptions.BookNotFoundException;
import com.example.springdb2.book.exceptions.EmptyBookUpdateRequestException;
import com.example.springdb2.book.mappers.BookMapper;
import com.example.springdb2.book.model.Book;
import com.example.springdb2.book.repository.BookRepository;
import com.example.springdb2.book.service.command.BookCommandService;
import com.example.springdb2.student.exceptions.StudentNotFoundException;
import com.example.springdb2.student.model.Student;
import com.example.springdb2.student.repository.StudentRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BookCommandServiceImpl implements BookCommandService {

    private BookRepository bookRepository;
    private StudentRepository studentRepository;

    public BookCommandServiceImpl(BookRepository bookRepository,StudentRepository studentRepository){
        this.bookRepository = bookRepository;
        this.studentRepository = studentRepository;

    }
    @Override
    @Transactional
    public BookResponse createBook(Long studentId, BookCreateRequest req) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        if(bookRepository.existsByNameIgnoreCase(req.name())) {
            throw new BookAlreadyExistsException();
        }

        Book book = BookMapper.toEntity(req);
        Book savedBook = bookRepository.save(book);
        student.addBook(savedBook);
        return BookMapper.toDto(savedBook);
    }
    @Override
    @Transactional
    public BookResponse patchBook(Long bookId, BookPatchRequest req) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        if(req.name() != null && !req.name().isBlank()){
            book.setName(req.name());
        }
        Book savedBook = bookRepository.save(book);
        return BookMapper.toDto(savedBook);
    }
    @Override
    @Transactional
    public BookResponse updateBook(Long bookId, BookPutRequest req) {
        if(req.name() == null) {
            throw new EmptyBookUpdateRequestException();
        }
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        if(!bookRepository.existsByNameIgnoreCase(req.name())){
            book.setName(req.name());
        } else throw new BookAlreadyExistsException();
        Book savedBook = bookRepository.save(book);
        return BookMapper.toDto(savedBook);
    }
    @Override
    @Transactional
    public BookResponse deleteBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        Book deleted = book;
        bookRepository.delete(book);
        return BookMapper.toDto(deleted);
    }
}
