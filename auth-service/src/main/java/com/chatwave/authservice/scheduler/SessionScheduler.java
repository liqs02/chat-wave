package com.chatwave.authservice.scheduler;

import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SessionScheduler {
    private final SessionRepository repository;

    /**
     * Changes accessToken and refreshToken to null if session is expired.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredSessions() {
        var sessions =
                repository.findAllExpiredNotCleaned()
                .parallelStream()
                .peek(Session::expire)
                .toList();

        repository.saveAll(sessions);
    }
}
