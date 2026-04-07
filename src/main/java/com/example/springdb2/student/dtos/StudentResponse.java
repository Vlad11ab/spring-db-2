package com.example.springdb2.student.dtos;

import com.example.springdb2.book.dtos.BookResponse;

import java.util.List;


public record StudentResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String password,
        Integer age,
        Integer nrCrediteNecesare,
        Integer nrCrediteEfectuate,
        List<BookResponse> books
){}