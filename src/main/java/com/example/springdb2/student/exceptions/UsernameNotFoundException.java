package com.example.springdb2.student.exceptions;

public class UsernameNotFoundException extends RuntimeException {
    public UsernameNotFoundException() {
        super("USERNAME_NOT_FOUND_EXCEPTION");
    }
}
