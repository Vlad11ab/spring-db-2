package com.example.springdb2.book.service;

import com.example.springdb2.book.dtos.BookCreateRequest;
import com.example.springdb2.book.dtos.BookPatchRequest;
import com.example.springdb2.book.dtos.BookPutRequest;
import com.example.springdb2.book.dtos.BookResponse;
import com.example.springdb2.book.exceptions.BookAlreadyExistsException;
import com.example.springdb2.book.exceptions.BookNotFoundException;
import com.example.springdb2.book.exceptions.EmptyBookUpdateRequestException;
import com.example.springdb2.book.model.Book;
import com.example.springdb2.book.repository.BookRepository;
import com.example.springdb2.book.service.command.BookCommandService;
import com.example.springdb2.book.service.command.impl.BookCommandServiceImpl;
import com.example.springdb2.student.exceptions.StudentNotFoundException;
import com.example.springdb2.student.model.Student;
import com.example.springdb2.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookCommandServiceImplTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private StudentRepository studentRepository;
    private BookCommandService bookCommandService;

    @BeforeEach
    void setUp(){
        bookCommandService = new BookCommandServiceImpl(bookRepository,studentRepository);
    }

    @Test
    void deleteThrowsNotFound(){
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class,()->bookCommandService.deleteBook(1L));
    }

    @Test
    void deleteRemovesWhenExisting(){
        Student student = new Student();
        Book book = Book.builder().id(1).name("Book").student(student).build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        bookCommandService.deleteBook(1L);
        verify(bookRepository).delete(book);
    }

    @Test
    void createBookThrowsStudentNotFound(){
        BookCreateRequest request = new BookCreateRequest("Spring Boot in Action");
//        Book book = Book.builder().id(1)
//                .name("Spring Boot in Action")
//                .build();
//        Student student = Student.builder().id(1L).books(List.of(book)).firstName("Vlad").lastName("Breazu").email("email@gmail.com").age(22).password("parola").nrCrediteNecesare(100).nrCrediteEfectuate(50).build();
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class,()->bookCommandService.createBook(1L,request));

    }

    @Test
    void createBookThrowsBookAlreadyExistsException(){
        BookCreateRequest request = new BookCreateRequest("Spring Boot in Action");
        Book book = Book.builder().id(1)
                .name("Spring Boot in Action")
                .build();
        Student student = Student.builder().id(1L).books(List.of(book)).firstName("Vlad").lastName("Breazu").email("email@gmail.com").age(22).password("parola").nrCrediteNecesare(100).nrCrediteEfectuate(50).build();
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(bookRepository.existsByNameIgnoreCase(request.name())).thenReturn(true);
        assertThrows(BookAlreadyExistsException.class,()->bookCommandService.createBook(1L,request));
    }

    @Test
    void createBookPersistsAndReturns(){
        BookCreateRequest request = new BookCreateRequest("Just A book");
        Student student = Student.builder().id(1L).firstName("Vlad").lastName("Breazu").email("email@gmail.com").age(22).password("parola").nrCrediteNecesare(100).nrCrediteEfectuate(50).build();
        Book toSave = Book.builder().name("Just A book").build();
        Book saved = Book.builder().id(1).name("Just A book").student(student).build();
        BookResponse expected = new BookResponse(1,"Just A book");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(bookRepository.existsByNameIgnoreCase(request.name())).thenReturn(false);
        when(bookRepository.save(toSave)).thenReturn(saved);

        BookResponse actual = bookCommandService.createBook(1L,request);

        assertEquals(expected,actual);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void patchThrowsWhenNotFound(){
        BookPatchRequest request = new BookPatchRequest("Just A book");

        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class,()->bookCommandService.patchBook(1L,request));
    }

    @Test
    void patchUpdatesAllFields(){
        BookPatchRequest request = new BookPatchRequest("Book");
        Book book = Book.builder().id(1).name("Just A book").build();
        List<Book> books = new ArrayList<>();
        books.add(book);
        Student student = Student.builder().id(1L).books(books).firstName("Vlad").lastName("Breazu").email("email@gmail.com").age(22).password("parola").nrCrediteNecesare(100).nrCrediteEfectuate(50).build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book toSave = Book.builder().id(1).name("Book").build();

        when(bookRepository.save(toSave)).thenReturn(toSave);

        BookResponse expected = new BookResponse(1,"Book");
        BookResponse actual = bookCommandService.patchBook(1L,request);
        assertEquals(expected.name(),actual.name());


    }

    @Test
    void updateThrowsWhenEmptyPayload(){
        Book existing = new Book();
        existing.setId(1);
        BookPutRequest emptyRequest = new BookPutRequest(null);
        assertThrows(EmptyBookUpdateRequestException.class,()->bookCommandService.updateBook(1L,emptyRequest));
    }

    @Test
    void updateThrowsBookNotFoundException(){
        Book existing = new Book();
        existing.setId(1);
        BookPutRequest request = new BookPutRequest("Update");

        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class,()->bookCommandService.updateBook(1L,request));
    }

    @Test
    void updateThrowsBookAlreadyExistsException(){
        Book existing = Book.builder().id(1).name("Book").build();
        BookPutRequest request = new BookPutRequest("Book");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bookRepository.existsByNameIgnoreCase("Book")).thenReturn(true);
        assertThrows(BookAlreadyExistsException.class,()->bookCommandService.updateBook(1L,request));
    }

    @Test
    void updateReplacesAllFields(){
        Book existing = Book.builder().id(1).name("Book").build();
        BookPutRequest request = new BookPutRequest("Update");
        Book toSave = Book.builder().id(1).name("Update").build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bookRepository.existsByNameIgnoreCase("Update")).thenReturn(false);
        when(bookRepository.save(toSave)).thenReturn(toSave);

        BookResponse expected = new BookResponse(1,"Update");
        BookResponse actual = bookCommandService.updateBook(1L,request);
        assertEquals(expected,actual);
    }

}
