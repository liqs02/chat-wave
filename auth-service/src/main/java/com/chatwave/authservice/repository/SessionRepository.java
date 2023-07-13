package com.chatwave.authservice.repository;

import com.chatwave.authservice.domain.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long>  {
    Optional<Session> findByRefreshToken(String refreshToken);
    Optional<Session> findByAccessToken(String accessToken);
}

