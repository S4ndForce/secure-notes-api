package com.example.jobs;

import com.example.shared.SharedLinkRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class SharedLinkCleanupJob {

    private static final Logger log = LoggerFactory.getLogger("JOBS");
    private final SharedLinkRepository repo;

    public SharedLinkCleanupJob(SharedLinkRepository repo) {
        this.repo = repo;
    }
    @Transactional
    @Scheduled(fixedDelay = 60_000) // every 60s
    public void cleanupExpired() {
        MDC.put("traceId", UUID.randomUUID().toString().substring(0, 8));
        try {
            int deleted = repo.deleteByExpiresAtBefore(Instant.now());
            if (deleted > 0) {
                log.info("Cleanup shared links: deleted={}", deleted);
            }
        } finally {
            MDC.clear();
        }
    }
}
