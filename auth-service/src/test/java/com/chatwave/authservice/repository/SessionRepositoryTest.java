package com.chatwave.authservice.repository;

import com.chatwave.authservice.domain.User;
import com.chatwave.authservice.domain.session.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("SessionRepository")
public class SessionRepositoryTest {
    @Autowired
    private SessionRepository repository;
    @Autowired
    private UserRepository userRepository;
    private Session session;
    private User user;

    @BeforeEach
    void setup() {
        repository.deleteAll();
        user = new User();
        user.setId(1);
        user.setPassword("pass");
        userRepository.save(user);

        session = new Session();
        session.setUser(user);
        session.setAccessToken("access");
        repository.save(session);
    }

    @Test
    @DisplayName("findById()")
    public void findById() {
        var exists = repository.findById(session.getId());
        if(exists.isEmpty()) fail();

        var foundSession = exists.get();

        assertEquals(session, foundSession);
    }

    @Test
    @DisplayName("findByRefreshToken()")
    public void findByRefreshToken() {
        var exists = repository.findByRefreshToken(session.getRefreshToken());
        if(exists.isEmpty()) fail();

        var foundSession = exists.get();

        assertEquals(session, foundSession);
    }

    @Test
    @DisplayName("findByAccessToken()")
    public void findByAccessToken() {
        var exists = repository.findByAccessToken(session.getAccessToken());
        if(exists.isEmpty()) fail();

        var foundSession = exists.get();

        assertEquals(session, foundSession);
    }

    @Test
    @DisplayName("findByAccessToken()")
    public void findAllByUserId() {
        var exists = repository.findByAccessToken(session.getAccessToken());
        if(exists.isEmpty()) fail();

        var foundSession = exists.get();

        assertEquals(session, foundSession);
    }

    @Test
    @DisplayName("findAllNotExpiredById()")
    public void findAllNotExpiredById() {
        var session1 = session;

        var session2 = new Session();
        session2.setUser(user);
        repository.save(session2);

        var session3 = new Session();
        session3.setUser(user);
        session3.setExpireDate(LocalDate.now());
        repository.save(session3);

        assertEquals(3, repository.findAll().size());

        var sessions = repository.findAllNotExpiredByUserId(1);

        assertEquals(List.of(session1, session2), sessions);
    }

    @Nested
    @DisplayName("findNotExpiredByAccessToken()")
    class findNotExpiredByAccessToken {
        @Test
        @DisplayName("should returns valid session")
        public void t1() {
            var optional = repository.findNotExpiredByAccessToken("access");
            assertEquals(session, optional.get());
        }

        @Test
        @DisplayName("shouldn't return session if access token is expired")
        public void t2() {
            session.setAccessTokenExpireDate(LocalDateTime.now());
            repository.save(session);

            var optional = repository.findNotExpiredByAccessToken("access");
            assertEquals(true, optional.isEmpty());
        }

        @Test
        @DisplayName("shouldn't return session if is expired")
        public void t3() {
            session.setExpireDate(LocalDate.now());
            repository.save(session);

            var optional = repository.findNotExpiredByAccessToken("access");
            assertEquals(true, optional.isEmpty());
        }
    }

    @Nested
    @DisplayName("findNotExpiredByIdAndUserId( sessionId, userId )")
    class findByIdAndUserId {
        @Test
        @DisplayName("should find session")
        public void t1() {
            var found = repository.findNotExpiredByIdAndUserId(session.getId(),1).get();
            assertEquals(session, found);
        }

        @Test
        @DisplayName("shouldn't find session if userId is invalid")
        public void t2() {
            var found = repository.findNotExpiredByIdAndUserId(session.getId(),2);
            assertTrue(found.isEmpty());
        }

        @Test
        @DisplayName("shouldn't find session if sessionId is invalid")
        public void t3() {
            var found = repository.findNotExpiredByIdAndUserId(0L,1);
            assertTrue(found.isEmpty());
        }
    }

    @Nested
    @DisplayName("findAllExpiredWithAccessOrRefreshToken()")
    class findAllExpiredWithAccessOrRefreshToken {
        @Test
        @DisplayName("should find expired sessions that have a accessToken or refreshToken")
        public void t1() {
            // session with refreshToken
            var session1 = new Session(user);
            session1.setAccessToken(null);
            session1.setExpireDate(LocalDate.now());

            // expired session with accessToken
            var session2 = new Session(user);
            session2.setRefreshToken(null);
            session2.setExpireDate(LocalDate.now());

            var session3 = new Session(user);
            session3.setRefreshToken(null);
            session3.setAccessToken(null);
            session3.setExpireDate(LocalDate.now());

            repository.saveAll(List.of(session1, session2, session3));


            var found = repository.findAllExpiredWithAccessOrRefreshToken();
            assertEquals(
                    List.of(session1, session2), found
            );
        }
    }
}