package com.chatwave.authservice.repository;

import com.chatwave.authservice.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID>  {
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expirationAt <= CURRENT_DATE")
    void deleteExpired();
}

