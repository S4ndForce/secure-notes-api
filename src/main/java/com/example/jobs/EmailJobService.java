package com.example.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailJobService {

    private static final Logger log = LoggerFactory.getLogger("JOBS");

    @Async("appExecutor")
    public void sendWelcomeEmail(String email) {
        String traceId = MDC.get("traceId"); // Null possible unless propagated
        log.info("Start sendWelcomeEmail email={} traceId={}", email, traceId);

        try {
            Thread.sleep(1500); // Work simulation
        } catch (InterruptedException ignored) {}

        log.info("Done sendWelcomeEmail email={} traceId={}", email, traceId);
    }
}