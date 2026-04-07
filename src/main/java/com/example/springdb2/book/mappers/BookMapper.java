package com.example.springdb2.book.mappers;

import com.example.springdb2.book.dtos.BookCreateRequest;
import com.example.springdb2.book.dtos.BookResponse;
import com.example.springdb2.book.model.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public static Book toEntity(BookCreateRequest req){
        if (req == null) return null;

        return Book.builder()
                .name(req.name())
                .build();
    }

    public static BookResponse toDto(Book book){

        return new BookResponse(
                book.getId(),
                book.getName()
                );
    }
}
