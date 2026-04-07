package com.example.springdb2.student.controller;

import com.example.springdb2.config.jwt.JWTTokenProvider;
import com.example.springdb2.config.security.SecurityConstants;
import com.example.springdb2.student.dtos.*;
import com.example.springdb2.student.service.command.StudentCommandService;
import com.example.springdb2.student.service.query.StudentQueryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books-students")
@Slf4j
public class StudentController {

    private StudentQueryService studentQueryService;
    private StudentCommandService studentCommandService;
    private final AuthenticationManager authenticationManager;
    private final JWTTokenProvider jwtTokenProvider;



    public StudentController(StudentQueryService studentQueryService, StudentCommandService studentCommandService, AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider){
        this.studentQueryService = studentQueryService;
        this.studentCommandService = studentCommandService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/students/students-with-books") //get all students with books
    public ResponseEntity<List<StudentResponse>> getAll(){
        log.info("HTTP GET /api/v1/books-students/students-with-books");
        return ResponseEntity.status(HttpStatus.OK).body(studentQueryService.getAllStudents());
    }

    @GetMapping("/students/get/{studentId}")
    public ResponseEntity<StudentResponse> getById(@PathVariable Long studentId){
        log.info("HTTP GET /api/v1/books/{}", studentId);
        return ResponseEntity.status(HttpStatus.OK).body(studentQueryService.getStudentById(studentId));
    }

    @PatchMapping("/students/edit/patch/{studentId}")
    public ResponseEntity<StudentResponse> patch(@PathVariable Long studentId, @Valid @RequestBody StudentPatchRequest patched){
        log.info("HTTP PATCH /api/v1/books-students/edit/patch/{}", studentId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(studentCommandService.patchStudent(studentId,patched));
    }

    @PostMapping("/students/register") //create student without books
    public ResponseEntity<StudentCreateResponse> create(@Valid @RequestBody StudentCreateRequest created){
        log.info("HTTP POST /api/v1/books-students/add");

        StudentCreateResponse studentCreateResponse= studentCommandService.createStudent(created);
        authenticate(created.email(),created.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(studentCreateResponse);
    }

    @DeleteMapping("/students/{studentId}")
    public ResponseEntity<StudentResponse> delete(@PathVariable Long studentId){
        log.info("HTTP DELETE /api/v1/books-students/{}", studentId);
        return ResponseEntity.status(HttpStatus.OK).body(studentCommandService.deleteStudent(studentId));
    }

    @PutMapping("/students/edit/update/{studentId}")
    public ResponseEntity<StudentResponse> update(@PathVariable Long studentId, @Valid @RequestBody StudentPutRequest updated){
        log.info("HTTP PUT /api/v1/books-students/edit/update/{studentId}", studentId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(studentCommandService.updateStudent(studentId, updated));
    }
    private HttpHeaders getJwtHeader(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(SecurityConstants.JWT_TOKEN_HEADER, token);
        return headers;
    }

    private void authenticate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }
}
