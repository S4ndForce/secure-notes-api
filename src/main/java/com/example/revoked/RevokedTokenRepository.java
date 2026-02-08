package com.example.revoked;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RevokedTokenRepository
        extends JpaRepository<RevokedToken, String> {

    boolean existsByJti(String jti);
}
