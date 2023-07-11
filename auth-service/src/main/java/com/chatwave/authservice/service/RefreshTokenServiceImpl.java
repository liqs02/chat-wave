package com.chatwave.authservice.service;

import com.chatwave.authservice.domain.RefreshToken;
import com.chatwave.authservice.domain.User;
import com.chatwave.authservice.repository.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.apache.http.util.Asserts.notNull;

@Service
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private RefreshTokenRepository repository;

    /**
     * {@inheritDoc}
     */
    @Override
    public RefreshToken create(User user) {
        var refreshToken = new RefreshToken();
        refreshToken.setUser(user);

        repository.save(refreshToken);
        log.info("new refresh token has been created: " + refreshToken.getId());

        return refreshToken;
    }

    @Autowired
    public void setRepository(RefreshTokenRepository repository) {
        notNull(repository, "RefreshTokenRepository can not be null!");
        this.repository = repository;
    }
}
