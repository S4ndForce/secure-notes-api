package com.example.failure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JobFailureRepository extends JpaRepository<JobFailure, UUID> {
}

