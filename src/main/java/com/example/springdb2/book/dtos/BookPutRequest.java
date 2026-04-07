package com.example.springdb2.book.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BookPutRequest(
        @NotBlank
        @Size(min = 3, max = 50, message = "size 3-50")
        String name
){}
