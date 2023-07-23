package com.chatwave.authservice.service;

import com.chatwave.authservice.domain.User;
import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.repository.UserRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@Setter(onMethod_=@Autowired)
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

        log.info("user has been authenticated: " + user.getId());
        return sessionService.createSession(user);
    }
}
