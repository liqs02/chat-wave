package com.chatwave.authservice.service;

import com.chatwave.authservice.domain.Session;
import com.chatwave.authservice.domain.User;
import com.chatwave.authservice.repository.SessionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.apache.commons.lang.Validate.notNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@Slf4j
public class SessionServiceImpl implements SessionService {
    private SessionRepository repository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Session createSession(User user) {
        var session = new Session(user);

        repository.save(session);
        log.info("new session has been created: " + session.getId());

        return session;
    }

    @Override
    public Optional<Session> getActiveSession(String accessToken) {
        var optionalSession = repository.findByAccessToken(accessToken);
        if(optionalSession.isEmpty()) return Optional.empty();

        var session = optionalSession.get();
        if(session.isAccessTokenExpired()) return Optional.empty();

        return Optional.of(session);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Session refreshSession(String refreshToken) {
        var session = repository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Invalid refresh token."));

        if(!session.isAccessTokenExpired() || session.isExpired()) {
            log.info("User tried to use inactive token. Possible theft of refresh token from a user. Session id: " + session.getId());
            throw new ResponseStatusException(BAD_REQUEST, "Invalid refresh token.");
        }

        session.refreshTokens();
        repository.save(session);

        return session;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivateSession(Long sessionId) {
        var session = repository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Session with given ID does not exist."));
        session.deactivate();
        repository.save(session);
    }

    @Autowired
    public void setRepository(SessionRepository repository) {
        notNull(repository, "SessionRepository can not be null!");
        this.repository = repository;
    }
}
