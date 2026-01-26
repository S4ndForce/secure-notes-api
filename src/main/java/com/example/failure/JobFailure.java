package com.example.failure;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "job_failures")
public class JobFailure {

    @Id
    @GeneratedValue
    private UUID id;

    private String jobType;
    private String actionKey;
    private String payload;
    private String errorMessage;

    private Instant failedAt = Instant.now();

    protected JobFailure() {}

    public JobFailure(
            String jobType,
            String actionKey,
            String payload,
            String errorMessage
    ) {
        this.jobType = jobType;
        this.actionKey = actionKey;
        this.payload = payload;
        this.errorMessage = errorMessage;
    }
}

