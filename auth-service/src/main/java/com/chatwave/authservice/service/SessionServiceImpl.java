package com.chatwave.authservice.service;

import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.domain.user.UserAuthentication;
import com.chatwave.authservice.repository.SessionRepository;
import com.chatwave.authservice.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Session createSession(Integer userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "User with given id does not exist."));

        var session = new Session(user);
        sessionRepository.save(session);
        log.info("new session has been created: " + session.getId());

        return session;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Session refreshSession(String refreshToken)  {
        var session = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Invalid refresh token."));

        if(!session.isAccessTokenExpired() || session.isExpired()) {
            log.info("User tried to use expired token. Possible theft of refresh token from a user. SessionId: " + session.getId());
            throw new ResponseStatusException(BAD_REQUEST, "Invalid refresh token.");
        }

        session.refreshTokens();
        sessionRepository.save(session);

        return session;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAuthentication getAuthentication(HttpServletRequest request) {
        var authHeader = request.getHeader("User-Authorization");
        if(authHeader == null)
            throw new ResponseStatusException(BAD_REQUEST, "No accessToken is provided");

        if(!authHeader.startsWith("Bearer "))
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid accessToken");

        var accessToken = authHeader.substring(7);
        var session = sessionRepository.findNotExpiredByAccessToken(accessToken)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Invalid accessToken"));

        return new UserAuthentication(session, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Session> getNotExpiredSessionsByUserId(Integer userId) {
        return sessionRepository.findAllNotExpiredByUserId(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void expireSessionsByUserId(Integer userId) {
        var sessions = sessionRepository.findAllNotExpiredByUserId(userId);
        if(sessions.isEmpty()) return;

        sessionRepository.saveAll(
                sessions.parallelStream()
                        .peek(Session::expire)
                        .toList()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void expireSession(Long sessionId, Integer userId) {
        var session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "The session with given ID does not exist."));

        var sessionUserId = session.getUser().getId();

        if(!sessionUserId.equals(userId)) {
            log.info("user with id " + userId + " tried to expire other user's session with id " + sessionId);
            throw new ResponseStatusException(NOT_FOUND, "The session with given ID does not exist.");
        }

        if(session.isExpired())
            throw new ResponseStatusException(BAD_REQUEST, "The session has been already expired.");

        session.expire();
        sessionRepository.save(session);
    }
}
