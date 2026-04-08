package com.example.springdb2.auth.service.impl;

import com.example.springdb2.auth.dtos.AuthLoginRequest;
import com.example.springdb2.auth.dtos.AuthResponse;
import com.example.springdb2.auth.service.AuthService;
import com.example.springdb2.config.security.PermissionGroup;
import com.example.springdb2.config.jwt.JWTTokenProvider;
import com.example.springdb2.student.dtos.StudentCreateRequest;
import com.example.springdb2.student.dtos.StudentCreateResponse;
import com.example.springdb2.student.exceptions.UsernameNotFoundException;
import com.example.springdb2.student.model.Student;
import com.example.springdb2.student.service.command.StudentCommandService;
import com.example.springdb2.student.service.query.StudentQueryService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.EnumSet;

@Service
public class AuthServiceImpl implements AuthService {
    private final StudentCommandService studentCommandService;
    private final StudentQueryService studentQueryService;
    private final AuthenticationManager authenticationManager;
    private final JWTTokenProvider jwtTokenProvider;

    public AuthServiceImpl(StudentCommandService studentCommandService,
                           StudentQueryService studentQueryService,
                           AuthenticationManager authenticationManager,
                           JWTTokenProvider jwtTokenProvider) {
        this.studentCommandService = studentCommandService;
        this.studentQueryService = studentQueryService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public StudentCreateResponse register(StudentCreateRequest request) {
        return studentCommandService.createStudent(request);
    }

    @Override
    public AuthResponse login(AuthLoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        Student student = studentQueryService.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException(request.email()));

        return new AuthResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getPermissionGroups() == null || student.getPermissionGroups().isEmpty()
                        ? Collections.emptySet()
                        : EnumSet.copyOf(student.getPermissionGroups()),
                student.getPermissions() == null || student.getPermissions().isEmpty()
                        ? Collections.emptySet()
                        : EnumSet.copyOf(student.getPermissions()),
                student.getEffectivePermissions().isEmpty()
                        ? Collections.emptySet()
                        : EnumSet.copyOf(student.getEffectivePermissions()),
                jwtTokenProvider.generateToken(student)
        );
    }
}
