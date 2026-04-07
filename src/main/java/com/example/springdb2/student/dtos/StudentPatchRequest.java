package com.example.springdb2.student.dtos;

public record StudentPatchRequest(
        String email,
        String password,
        Integer nrCrediteEfectuate

){}
