package com.example.idempotency;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "idempotent_actions",
        uniqueConstraints = @UniqueConstraint(columnNames = "actionKey")
)
public class IdempotentAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String actionKey;

    @Column(nullable = false)
    private String status; // IN_PROGRESS, COMPLETED

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected IdempotentAction() {}

    public IdempotentAction(String actionKey, String status) {
        this.actionKey = actionKey;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void markCompleted() {
        this.status = "COMPLETED";
    }
}
