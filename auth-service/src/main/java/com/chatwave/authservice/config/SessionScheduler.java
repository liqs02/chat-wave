package com.chatwave.authservice.config;

import com.chatwave.authservice.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang.Validate.notNull;

@Component
public class SessionScheduler { // TODO: add tests
    private SessionRepository repository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredSessionWithTokens() {
        var sessionList = repository.findAllExpiredWithAccessOrRefreshToken();
        for(var session : sessionList)
            session.expire();

        repository.saveAll(sessionList);
    }

    @Autowired
    public void setRefreshTokenRepository(SessionRepository repository) {
        notNull(repository, "SessionRepository can not be null!");
        this.repository = repository;
    }
}
