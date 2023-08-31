package com.chatwave.authservice.config;

import com.chatwave.authservice.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionScheduler { // TODO: add tests
    private final SessionRepository repository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredSessionWithTokens() {
        var sessionList = repository.findAllExpiredWithAccessOrRefreshToken();
        for(var session : sessionList)
            session.expire();

        repository.saveAll(sessionList);
    }
}
