package com.chatwave.authservice.repository;

import com.chatwave.authservice.domain.Session;
import com.chatwave.authservice.domain.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("SessionRepository")
public class SessionRepositoryTest {
    @Autowired
    private SessionRepository repository;
    @Autowired
    private UserRepository userRepository;
    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    Session createSession() {
        var user = new User();
        user.setId(1);
        user.setPassword("pass");
        userRepository.save(user);

        var session = new Session();
        session.setUser(user);

        repository.save(session);
        return session;
    }

    @Test
    @DisplayName("save() and findById()")
    public void findById() {
        var session = createSession();

        var exists = repository.findById(session.getId());
        if(exists.isEmpty()) fail();

        var foundSession = exists.get();

        assertEquals(session, foundSession);
    }

    @Test
    @DisplayName("save() and findByRefreshToken()")
    public void findByRefreshToken() {
        var session = createSession();

        var exists = repository.findByRefreshToken(session.getRefreshToken());
        if(exists.isEmpty()) fail();

        var foundSession = exists.get();

        assertEquals(session, foundSession);
    }

    @Test
    @DisplayName("save() and findByAccessToken()")
    public void findByAccessToken() {
        var session = createSession();

        var exists = repository.findByAccessToken(session.getAccessToken());
        if(exists.isEmpty()) fail();

        var foundSession = exists.get();

        assertEquals(session, foundSession);
    }
}