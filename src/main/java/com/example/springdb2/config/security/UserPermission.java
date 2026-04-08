package com.example.springdb2.config.security;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserPermission {
    STUDENT_READ("student:read"),
    STUDENT_EDIT("student:edit"),
    STUDENT_PERMISSIONS_EDIT("student:permissions:edit"),
    BOOK_READ("book:read"),
    BOOK_EDIT("book:edit"),
    BOOK_WRITE("book:write");

    private final String permission;

    public String getPermission(){
        return permission;
    }
}
