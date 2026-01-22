package com.example.idempotency;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class IdempotencyService {

    private final IdempotentActionRepository repo;

    public IdempotencyService(IdempotentActionRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public boolean tryStart(String actionKey) {
        return repo.findByActionKey(actionKey)
                .map(existing -> false)
                .orElseGet(() -> {
                    repo.save(new IdempotentAction(actionKey, "IN_PROGRESS"));
                    return true;
                });
    }

    @Transactional
    public void markCompleted(String actionKey) {
        repo.findByActionKey(actionKey)
                .ifPresent(IdempotentAction::markCompleted);
    }
}
