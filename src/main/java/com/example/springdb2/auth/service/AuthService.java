package com.example.springdb2.auth.service;

import com.example.springdb2.auth.dtos.AuthLoginRequest;
import com.example.springdb2.auth.dtos.AuthResponse;
import com.example.springdb2.student.dtos.StudentCreateRequest;
import com.example.springdb2.student.dtos.StudentCreateResponse;

public interface AuthService {
    StudentCreateResponse register(StudentCreateRequest request);
    AuthResponse login(AuthLoginRequest request);
}
