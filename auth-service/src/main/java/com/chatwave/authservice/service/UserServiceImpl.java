package com.chatwave.authservice.service;

import com.chatwave.authservice.domain.Session;
import com.chatwave.authservice.domain.User;
import com.chatwave.authservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.apache.commons.lang.Validate.notNull;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private PasswordEncoder passwordEncoder;
    private UserRepository repository;
    private SessionService sessionService;
    private AuthenticationManager authManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public Session createUser(User user){
        var existing = repository.findById(user.getId());
        if(existing.isPresent()) {
            log.warn("Possible data inconsistency! Client tried to create user with busy ID: " + user.getId());
            throw new ResponseStatusException(CONFLICT, "User with given id already exists.");
        }

        var hash = passwordEncoder.encode(user.getPassword());
        user.setPassword(hash);

        repository.save(user);
        log.info("new user has been created: " + user.getId());

        return sessionService.createSession(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Session authenticateUser(User user) {
        authenticateCredentials(user);
        log.info("user has been authenticated: " + user.getId());

        return sessionService.createSession(user);
    }

    private void authenticateCredentials(User user) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getId(),
                            user.getPassword()
                    )
            );
        } catch(Exception e) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid username or password.");
        }
    }


    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        notNull(passwordEncoder, "PasswordEncoder can not be null!");
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setRepository(UserRepository repository) {
        notNull(repository, "UserRepository can not be null!");
        this.repository = repository;
    }

    @Autowired
    public void setRefreshTokenService(SessionService sessionService) {
        notNull(sessionService, "SessionService can not be null!");
        this.sessionService = sessionService;
    }

    @Autowired
    public void setAuthManager(AuthenticationManager authManager) {
        notNull(authManager, "AuthenticationManager can not be null!");
        this.authManager = authManager;
    }
}
