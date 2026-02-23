package com.example.auth;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotNull(message = "Email cannot be empty")
        @Size(max = 200, message = "Email cannot exceed 200 characters")
        String email,
        @Size(min= 8, message = "Password cannot be less than 8 characters")
        @NotNull(message = "Password cannot be empty")
        String password
)
{}

