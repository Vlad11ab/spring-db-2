package com.example.springdb2.system.exceptions;

import com.example.springdb2.book.exceptions.BookAlreadyExistsException;
import com.example.springdb2.book.exceptions.BookNotFoundException;
import com.example.springdb2.book.exceptions.EmptyBookUpdateRequestException;
import com.example.springdb2.student.exceptions.EmptyStudentUpdateRequestException;
import com.example.springdb2.student.exceptions.StudentAlreadyExistsException;
import com.example.springdb2.student.exceptions.StudentNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public GlobalExceptionHandler(){}

    @ExceptionHandler({
            BookNotFoundException.class,
            StudentNotFoundException.class
    })
    public ResponseEntity<Map<String,Object>> handleNotFound(RuntimeException exception){
        Map<String,Object> body = new HashMap<>();
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "NOT FOUND");
        body.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler({
            BookAlreadyExistsException.class,
            StudentAlreadyExistsException.class
    })
    public ResponseEntity<Map<String,Object>> handleConflict(RuntimeException exception){
        Map<String,Object> body = new HashMap<>();
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "CONFLICT");
        body.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler({
            EmptyBookUpdateRequestException.class,
            EmptyStudentUpdateRequestException.class
    })
    public ResponseEntity<Map<String,Object>> handleBadRequest(RuntimeException exception){
        Map<String,Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "BAD REQUEST");
        body.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidation(MethodArgumentNotValidException exception){
        Map<String,Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Failed");
        body.put("message", "Validation Failed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String,Object>> handleConstraintViolation(ConstraintViolationException exception){
        Map<String,Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Failed");
        body.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String,Object>> handleMissingParameter(MissingServletRequestParameterException exception){
        Map<String,Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Missing Parameter");
        body.put("message", "Required request parameter is missing: " + exception.getParameterName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String,Object>> handleTypeMismatch(MethodArgumentTypeMismatchException exception){
        Map<String,Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Type Mismatch");
        body.put("message", "Invalid value for parameter: " + exception.getName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String,Object>> handleUnreadableMessage(HttpMessageNotReadableException exception){
        Map<String,Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Malformed Json");
        body.put("message", "Request body is missing or malformed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String,Object>> handleDataIntegrity(DataIntegrityViolationException exception){
        Map<String,Object> body = new HashMap<>();
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Data Integrity Violation");
        body.put("message", "Operation violates database integrity constraints");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleUnexpected(Exception exception){
        Map<String,Object> body = new HashMap<>();
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "An unexpected error ocurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }



}
