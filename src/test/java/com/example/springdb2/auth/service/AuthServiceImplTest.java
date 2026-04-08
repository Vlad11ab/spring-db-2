package com.example.springdb2.auth.service;

import com.example.springdb2.auth.dtos.AuthLoginRequest;
import com.example.springdb2.auth.dtos.AuthResponse;
import com.example.springdb2.auth.service.impl.AuthServiceImpl;
import com.example.springdb2.config.security.PermissionGroup;
import com.example.springdb2.config.security.UserPermission;
import com.example.springdb2.config.jwt.JWTTokenProvider;
import com.example.springdb2.student.dtos.StudentCreateRequest;
import com.example.springdb2.student.dtos.StudentCreateResponse;
import com.example.springdb2.student.exceptions.UsernameNotFoundException;
import com.example.springdb2.student.model.Student;
import com.example.springdb2.student.service.command.StudentCommandService;
import com.example.springdb2.student.service.query.StudentQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private StudentCommandService studentCommandService;

    @Mock
    private StudentQueryService studentQueryService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTTokenProvider jwtTokenProvider;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(studentCommandService, studentQueryService, authenticationManager, jwtTokenProvider);
    }

    @Test
    void registerDelegatesToStudentCommandService() {
        StudentCreateRequest request = new StudentCreateRequest("Vlad", "Breazu", "email@gmail.com", 22, "parola", 100, 50);
        StudentCreateResponse response = new StudentCreateResponse(
                1L, "Vlad", "Breazu", "email@gmail.com", 22, 100, 50,
                EnumSet.of(PermissionGroup.STUDENT_BASIC),
                Collections.emptySet(),
                EnumSet.of(UserPermission.STUDENT_READ, UserPermission.BOOK_READ, UserPermission.BOOK_WRITE),
                "jwt-token"
        );

        when(studentCommandService.createStudent(request)).thenReturn(response);

        StudentCreateResponse actual = authService.register(request);

        assertEquals(response, actual);
    }

    @Test
    void loginAuthenticatesAndReturnsToken() {
        AuthLoginRequest request = new AuthLoginRequest("email@gmail.com", "parola");
        Student student = Student.builder()
                .id(1L)
                .firstName("Vlad")
                .lastName("Breazu")
                .email("email@gmail.com")
                .password("{bcrypt}hash")
                .permissionGroups(EnumSet.of(PermissionGroup.STUDENT_BASIC))
                .permissions(Collections.emptySet())
                .build();

        when(studentQueryService.findByEmail(request.email())).thenReturn(Optional.of(student));
        when(jwtTokenProvider.generateToken(student)).thenReturn("jwt-token");

        AuthResponse actual = authService.login(request);

        assertEquals("jwt-token", actual.token());
        assertEquals("email@gmail.com", actual.email());
        assertEquals(EnumSet.of(PermissionGroup.STUDENT_BASIC), actual.permissionGroups());
        assertEquals(Collections.emptySet(), actual.directPermissions());
        assertEquals(EnumSet.of(UserPermission.STUDENT_READ, UserPermission.BOOK_READ, UserPermission.BOOK_WRITE), actual.effectivePermissions());
        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
    }

    @Test
    void loginThrowsWhenUserIsMissing() {
        AuthLoginRequest request = new AuthLoginRequest("missing@gmail.com", "parola");
        when(studentQueryService.findByEmail(request.email())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authService.login(request));
    }
}
