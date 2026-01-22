package com.example.jobs;

import com.example.idempotency.IdempotencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmailJobService {

    private static final Logger log = LoggerFactory.getLogger("JOBS");

    private final IdempotencyService idempotencyService;

    public EmailJobService(IdempotencyService idempotencyService) {
        this.idempotencyService = idempotencyService;
    }

    @Async("appExecutor")
    @Retryable(
            retryFor = {
                    RuntimeException.class
            },
            noRetryFor = {
                    IllegalArgumentException.class
            },
            maxAttempts = 3,
            backoff = @Backoff(
                    delay = 2000,
                    multiplier = 2,
                    maxDelay = 15000,
                    random = true
            )
    )
    public void sendWelcomeEmail(String email) {

        String actionKey = "welcome-email:" + email;

        if (!idempotencyService.tryStart(actionKey)) {
            log.info("Skipping duplicate welcome email userId={}", email);
            return;
        }

        log.info("Sending welcome email userId={} email={}", email);

        // Simulate email send
        simulateSend(email);

        idempotencyService.markCompleted(actionKey);

        log.info("Welcome email completed userId={}", email);
    }

    private void simulateSend(String email) {
        // replace with real email client
        if (Math.random() < 0.3) {
            throw new RuntimeException("Transient mail failure");
        }
    }

    @Recover
    public void recover(RuntimeException e, UUID userId, String email) {
        log.error(
                "Welcome email permanently failed userId={} email={}",
                userId,
                email,
                e
        );
        // later: persist failure, alert, or DLQ
    }
}
