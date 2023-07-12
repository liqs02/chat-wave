package com.chatwave.authservice.repository;

import com.chatwave.authservice.domain.RefreshToken;
import com.chatwave.authservice.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("RefreshTokenRepository")
public class RefreshTokenRepositoryTest {
    @Autowired
    private RefreshTokenRepository repository;
    @Autowired
    private UserRepository userRepository;
    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("should save and find refresh token by id")
    public void saveAndFindRefreshTokenById() {
        var user = new User();
        user.setId(1);
        user.setPassword("pass");
        userRepository.save(user);

        var token = new RefreshToken();
        token.setUser(user);

        repository.save(token);

        var exists = repository.findById(token.getId());
        if(exists.isEmpty()) fail();

        var foundToken = exists.get();
        assertTrue(foundToken
                .getId().toString()
                .matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")
        );


        var nextMonth = LocalDate.now().plusMonths(1);
        assertEquals(nextMonth, foundToken.getExpirationAt());

        assertNotNull(foundToken.getUser());
        assertEquals(1, foundToken.getUser().getId());
    }

    @Test
    @Transactional
    @DisplayName("should delete expired tokens")
    public void deleteExpired() throws NoSuchFieldException, IllegalAccessException {
        var user = new User();
        user.setId(1);
        user.setPassword("pass");
        userRepository.save(user);

        // create correct token
        var correctToken = new RefreshToken();
        correctToken.setUser(user);

        // create expired refresh token
        var expiredToken = new RefreshToken();
        expiredToken.setUser(user);

        var expiredField = RefreshToken.class.getDeclaredField("expirationAt");
        expiredField.setAccessible(true);
        expiredField.set(expiredToken, LocalDate.now().minusDays(1));

        repository.saveAll(
                List.of(correctToken, expiredToken)
        );

        repository.deleteExpired();

        var tokenList = repository.findAll();
        assertEquals(1, tokenList.size());
        assertEquals(correctToken.getId(), tokenList.get(0).getId());
    }
}