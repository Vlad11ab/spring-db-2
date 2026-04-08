package com.example.springdb2.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthLoginRequest(
        @Email
        @NotBlank
        @Size(min = 3, max = 50, message = "size 3-50")
        String email,

        @NotBlank
        @Size(min = 3, max = 50, message = "size 3-50")
        String password
) {}
