package com.example.springdb2.system.exceptions;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiErrorResponse(
        LocalDateTime localDate,
        String message,
        int status,
        Map<String, Object> details
){}
