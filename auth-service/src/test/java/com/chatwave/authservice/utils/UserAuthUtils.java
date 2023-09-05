package com.chatwave.authservice.utils;

import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.domain.user.User;
import com.chatwave.authservice.repository.SessionRepository;
import com.chatwave.authservice.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
public class UserAuthUtils {
    @Autowired
    protected WebTestClient webTestClient;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected SessionRepository sessionRepository;
    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected Session session;

    @BeforeEach
    void setUp() {
        var encoded = passwordEncoder.encode("Pass1234");

        var user = new User();
        user.setId(1);
        user.setPassword(encoded);

        userRepository.save(user);

        session = new Session(user);
        sessionRepository.save(session);
    }

    @AfterEach
    void tearDown() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
    }

    public String getAccessToken() {
        return session.getAccessToken();
    }

    public String getAuthHeader() {
        return "Bearer " + this.getAccessToken();
    }
}
