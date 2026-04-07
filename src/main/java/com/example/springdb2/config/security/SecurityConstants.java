package com.example.springdb2.config.security;

public class SecurityConstants {
    public static final String TOKEN_PREFIX = "Bearer";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String AUTHORITIES = "authorities";
    public static final String ISSUER = "sping-db-2-api";
    public static final String AUDIENCE = "sping-db-2-client";
    public static final String[] PUBLIC_URLS = {
            "/api/v1/books-students/students/login",
            "/api/v1/books-students/students/register"
    };
    private SecurityConstants(){
    }
}
