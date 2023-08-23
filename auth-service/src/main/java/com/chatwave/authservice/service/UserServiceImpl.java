package com.chatwave.authservice.service;

import com.chatwave.authservice.config.UserAuthFilter;
import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.domain.user.User;
import com.chatwave.authservice.domain.user.UserAuthentication;
import com.chatwave.authservice.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

@Service
@Setter(onMethod_=@Autowired)
@Slf4j
public class UserServiceImpl implements UserService {
    private PasswordEncoder passwordEncoder;
    private UserRepository repository;
    private SessionService sessionService;
    private AuthenticationManager authManager;
    private UserAuthFilter userAuthFilter;

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAuthentication getUserAuthentication(HttpServletRequest request) {
        var authentication = userAuthFilter.getUserAuthentication(request);
        if(authentication == null)
            throw new ResponseStatusException(BAD_REQUEST, "Invalid accessToken.");

        return authentication;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Session createUser(User user){
        var optional = repository.findById(user.getId());
        if(optional.isPresent()) {
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
            throw new ResponseStatusException(BAD_REQUEST, "Invalid login or password.");
        }

        log.info("user has been authenticated: " + user.getId());
        return sessionService.createSession(user);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void patchUserPassword(User user, String newPassword) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getId(),
                            user.getPassword()
                    )
            );
        } catch(Exception e) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid login or password.");
        }

        var found = repository.findById(user.getId()).get();

        var hash = passwordEncoder.encode(newPassword);
        found.setPassword(hash);

        repository.save(found);
    }
}
