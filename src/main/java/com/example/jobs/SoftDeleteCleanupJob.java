package com.example.jobs;

import com.example.note.NoteRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Component
public class SoftDeleteCleanupJob {

    private static final Logger log = LoggerFactory.getLogger("JOBS");
    private final NoteRepository notes;

    public SoftDeleteCleanupJob(NoteRepository notes) {
        this.notes = notes;
    }
    @Transactional
    @Scheduled(cron = "0 0 * * * *") // every hour
    public void purgeDeletedNotes() {
        MDC.put("traceId", UUID.randomUUID().toString().substring(0, 8));
        try {
            Instant cutoff = Instant.now().minus(30, ChronoUnit.DAYS);
            int purged = notes.deleteByDeletedAtBefore(cutoff);
            if (purged > 0) {
                log.info("Purged soft-deleted notes: {}", purged);
            }
        } finally {
            MDC.clear();
        }
    }
}
