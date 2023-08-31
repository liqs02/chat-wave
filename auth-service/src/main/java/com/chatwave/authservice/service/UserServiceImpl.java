package com.chatwave.authservice.service;

import com.chatwave.authservice.config.UserAuthFilter;
import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.domain.user.User;
import com.chatwave.authservice.domain.user.UserAuthentication;
import com.chatwave.authservice.repository.SessionRepository;
import com.chatwave.authservice.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final SessionRepository sessionRepository;
    private final SessionService sessionService;
    private final AuthenticationManager authManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAuthentication getUserAuthentication(HttpServletRequest request) {
        var authHeader = request.getHeader("User-Authorization");
        if(authHeader == null)
            return null;

        if(!authHeader.startsWith("Bearer "))
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid accessToken.");

        var accessToken = authHeader.substring(7);
        var optionalSession = sessionRepository.findNotExpiredByAccessToken(accessToken);

        if(optionalSession.isEmpty())
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid accessToken.");

        var session = optionalSession.get();
        return new UserAuthentication(session, request);
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
