package com.example.springdb2.auth.controller;

import com.example.springdb2.auth.dtos.AuthLoginRequest;
import com.example.springdb2.auth.dtos.AuthResponse;
import com.example.springdb2.auth.service.AuthService;
import com.example.springdb2.student.dtos.StudentCreateRequest;
import com.example.springdb2.student.dtos.StudentCreateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@Tag(name = "Authentication", description = "Public endpoints for account registration and JWT login")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register a student account",
            description = "Creates a new student account with the default permission group STUDENT_BASIC."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Student registered successfully",
                    content = @Content(schema = @Schema(implementation = StudentCreateResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "409", description = "A student with the same email already exists")
    })
    public ResponseEntity<StudentCreateResponse> register(@Valid @RequestBody StudentCreateRequest request) {
        log.info("HTTP POST /api/v1/auth/register");
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login and obtain a JWT token",
            description = "Authenticates a user and returns a JWT token together with permission groups, direct permissions and effective permissions."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthLoginRequest request) {
        log.info("HTTP POST /api/v1/auth/login");
        return ResponseEntity.ok(authService.login(request));
    }
}
