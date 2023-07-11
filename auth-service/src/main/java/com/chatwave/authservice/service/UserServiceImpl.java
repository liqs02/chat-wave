package com.chatwave.authservice.service;

import com.chatwave.authservice.domain.User;
import com.chatwave.authservice.domain.dto.UserTokenSet;
import com.chatwave.authservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.UUID;

import static org.apache.http.util.Asserts.notNull;
import static org.springframework.http.HttpStatus.CONFLICT;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private PasswordEncoder passwordEncoder;
    private UserRepository repository;
    private RefreshTokenService refreshTokenService;
    private JwtService jwtService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UserTokenSet create(User user){
        var existing = repository.findById(user.getId());
        if(existing.isPresent()) {
            log.warn("Possible data inconsistency! Client tried to create user with busy ID: " + user.getId());
            throw new ResponseStatusException(CONFLICT, "User with given id already exists.");
        }

        var hash = passwordEncoder.encode(user.getPassword());
        user.setPassword(hash);

        repository.save(user);
        log.info("new user has been created: " + user.getId());

        var refreshToken = refreshTokenService.create(user);
        var accessToken = jwtService.generateToken(user);

        return new UserTokenSet(refreshToken, accessToken);
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
    public void setRefreshTokenService(RefreshTokenService refreshTokenService) {
        notNull(refreshTokenService, "RefreshTokenService can not be null!");
        this.refreshTokenService = refreshTokenService;
    }

    @Autowired
    public void setJwtService(JwtService jwtService) {
        notNull(jwtService, "JwtService can not be null!");
        this.jwtService = jwtService;
    }
}

