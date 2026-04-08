package com.example.springdb2.config.security;

public class SecurityConstants {
    public static final String TOKEN_PREFIX = "Bearer";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String AUTHORITIES = "authorities";
    public static final String ISSUER = "spring-db-2-api";
    public static final String AUDIENCE = "spring-db-2-client";
    public static final String[] PUBLIC_URLS = {
            "/api/v1/auth/login",
            "/api/v1/auth/register"
    };
    private SecurityConstants(){
    }
}
