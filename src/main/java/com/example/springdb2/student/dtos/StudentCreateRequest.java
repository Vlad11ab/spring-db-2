package com.example.springdb2.student.dtos;

import jakarta.validation.constraints.*;

public record StudentCreateRequest(
        @NotBlank
        @Size(min = 3, max = 50, message = "size 3-50")
        String firstName,

        @NotBlank
        @Size(min = 3, max = 50, message = "size 3-50")
        String lastName,

        @Email
        @NotBlank
        @Size(min = 3, max = 50, message = "size 3-50")
        String email,

        @Positive(message = "Age >17")
        Integer age,

        @NotBlank
        @Size(min = 3, max = 50, message = "size 3-50")
        String password,

        @Positive(message = "Minim Credite Necesare = 35")
        @NotNull
        Integer nrCrediteNecesare,

        @PositiveOrZero
        Integer nrCrediteEfectuate

){}
