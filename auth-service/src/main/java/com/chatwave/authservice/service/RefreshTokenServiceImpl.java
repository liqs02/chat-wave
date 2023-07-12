package com.chatwave.authservice.service;

import com.chatwave.authservice.domain.RefreshToken;
import com.chatwave.authservice.domain.User;
import com.chatwave.authservice.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.apache.commons.lang.Validate.notNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

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

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public RefreshToken refresh(UUID tokenId) {
        var token = repository.findById(tokenId)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Invalid refresh token."));

        if(!token.isActive()) {
            log.info("User tried to use inactive token. Possible theft of refresh token from a user with id: " + token.getUser().getId());
            repository.delete(token);
            throw new ResponseStatusException(BAD_REQUEST, "Invalid refresh token.");
        }

        var newToken = create(token.getUser());
        repository.delete(token);

        return newToken;
    }

    @Override
    public void invalidate(UUID tokenId) {
        var token = repository.findById(tokenId)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Refresh token does not exist."));

        repository.delete(token);
    }

    @Autowired
    public void setRepository(RefreshTokenRepository repository) {
        notNull(repository, "RefreshTokenRepository can not be null!");
        this.repository = repository;
    }
}
