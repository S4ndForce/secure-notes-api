package com.example.revoked;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.Instant;

@Entity
public class RevokedToken {

    @Id
    private String jti;

    private Instant revokedAt;

    protected RevokedToken() {}

    public RevokedToken(String jti, Instant revokedAt) {
        this.jti = jti;
        this.revokedAt = revokedAt;
    }

    public String getJti() {
        return jti;
    }
}
