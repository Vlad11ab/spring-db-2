package com.example.springdb2.student.dtos;

public record StudentCreateResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String password,
        Integer age,
        Integer nrCrediteEfectuate,
        Integer nrCrediteNecesare,
        String token
        ){}