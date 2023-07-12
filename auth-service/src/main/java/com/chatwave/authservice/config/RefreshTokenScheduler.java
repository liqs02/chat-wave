package com.chatwave.authservice.config;

import com.chatwave.authservice.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang.Validate.notNull;

@Component
public class RefreshTokenScheduler {
    private RefreshTokenRepository repository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteExpiredRefreshTokens() {
        repository.deleteExpired();
    }

    @Autowired
    public void setRepository(RefreshTokenRepository repository) {
        notNull(repository, "RefreshTokenRepository can not be null!");
        this.repository = repository;
    }
}
