package com.example.springdb2.config.security;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserPermission {
    BOOK_READ("book:read"),
    BOOK_EDIT("book:edit"),
    BOOK_WRITE("book:write");

    private String permission;
    public String getPermission(){
        return permission;
    }
}
