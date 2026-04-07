package com.example.springdb2.book.service;

import com.example.springdb2.book.dtos.BookResponse;
import com.example.springdb2.book.exceptions.BookNotFoundException;
import com.example.springdb2.book.model.Book;
import com.example.springdb2.book.repository.BookRepository;
import com.example.springdb2.book.service.query.BookQueryService;
import com.example.springdb2.book.service.query.impl.BookQueryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookQueryServiceImplTest {

    @Mock
    private BookRepository bookRepository;
    private BookQueryService bookQueryService;

    @BeforeEach
    void setUp(){
        bookQueryService = new BookQueryServiceImpl(bookRepository);
    }

    @Test
    void getAllBooks(){
        List<Book> books = List.of(
                Book.builder().id(1)
                        .name("Spring Boot in Action")
                        .build(),

                Book.builder().id(2)
                        .name("Clean Code")
                        .build(),

                Book.builder().id(3)
                        .name("Effective Java")
                        .build(),

                Book.builder().id(4)
                        .name("Design Patterns")
                        .build(),

                Book.builder().id(5)
                        .name("Java Concurrency in Practice")
                        .build()
        );
        List<BookResponse> expectedList = List.of(
                new BookResponse(1, "Spring Boot in Action"),
                new BookResponse(2, "Clean Code"),
                new BookResponse(3, "Effective Java"),
                new BookResponse(4, "Design Patterns"),
                new BookResponse(5, "Java Concurrency in Practice")
        );

        when(bookRepository.findAll()).thenReturn(books);
        List<BookResponse> actualList = bookQueryService.getAllBooks();
        assertEquals(expectedList,actualList);

    }

    @Test
    void getBookByIdThrowsNotFoundException(){
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class,()->bookQueryService.getBookById(1L));

    }

    @Test
    void getBookById(){
        Book book1 = Book.builder().id(1)
                .name("Spring Boot in Action")
                .build();
        BookResponse expected = new BookResponse(1, "Spring Boot in Action");
        when(bookRepository.findById(1L)).thenReturn(Optional.ofNullable(book1));

        BookResponse actual = bookQueryService.getBookById(1L);
        assertEquals(expected,actual);
    }


}
