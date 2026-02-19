package com.example.note;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateSharedLinkRequest(

        @NotNull(message = "Expiration duration is required")
        @Positive(message = "Expiration must be greater than 0")
        @Max(value = 604800, message = "Expiration cannot exceed 7 days")
        Long expiresInSeconds

) {}

