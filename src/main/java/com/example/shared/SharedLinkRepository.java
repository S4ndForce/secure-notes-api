package com.example.shared;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface SharedLinkRepository extends JpaRepository<SharedLink, Long> {
    Optional<SharedLink> findByToken(String token);
    int deleteByExpiresAtBefore(Instant now);
}
