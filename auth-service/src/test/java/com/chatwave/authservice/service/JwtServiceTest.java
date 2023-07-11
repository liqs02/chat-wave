package com.chatwave.authservice.service;

import com.chatwave.authservice.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService")
public class JwtServiceTest {
    /**
     * mockedToken does not have expiration date!
     * id: 1
     * key: "value" (extraClaim)
     */
    private String mockedToken = "eyJhbGciOiJIUzI1NiJ9.eyJrZXkiOiJ2YWx1ZSIsInN1YiI6IjEiLCJpYXQiOjE2ODkxMTExNzB9.sJ_TERsglUqGPlz4zQgnLcUe62bBo27ldAH7adbdtPM";
    private JwtService service = new JwtServiceImpl();
    @BeforeEach
    public void setUp() {
        var secretKey =  "0".repeat(43);
        setField(service, "secretKey", secretKey);
    }

    public User createUser() {
        final var user = new User();
        user.setId(1);
        user.setPassword("");
        return user;
    }

    @Test
    @DisplayName("All JWT Service methods should work together correctly")
    public void testAll() {
        final var user = createUser();
        var token = service.generateToken(user);

        assertEquals("1",  service.extractUsername(token));
        assertEquals(true, service.isTokenValid(token, user));

        var claims = service.extractAllClaims(token);

        var now = new Date(System.currentTimeMillis());

        assertTrue(claims.getExpiration().after(now));
        assertTrue(claims.getIssuedAt().before(now));

        final var hashMap = new HashMap<String, Object>();
        hashMap.put("id", 1);
        token = service.generateToken(user, hashMap);

        claims = service.extractAllClaims(token);
        assertEquals(1, claims.get("id"));
    }

    @Nested
    @DisplayName("generateToken(userDetails)")
    class generateToken1 {
        @Test
        @DisplayName("should return valid token")
        public void generateToken2() {
            final var user = createUser();
            final var token = service.generateToken(user);

            assertNotNull(token);
            assertTrue(token.matches("^[\\w-]+\\.[\\w-]+\\.[\\w-]+$"), "Returned value is not valid JWT");
            assertTrue(token.startsWith("eyJhbGciOiJIUzI1NiJ9."));
        }

        @Test
        @DisplayName("should throw exception if secret key is incorrect")
        public void generateToken3() {
            final var user = createUser();
            setField(service, "secretKey", "invalidSecretKey");
            assertThrows(
                    ResponseStatusException.class,
                    () -> service.generateToken(user)
            );
        }
    }

    @Nested
    @DisplayName("generateToken(extraClaims, userDetails)")
    class generateToken2 {
        @Test
        @DisplayName("should return valid token")
        public void generateToken3() {
            final var hashMap = new HashMap<String, Object>();
            hashMap.put("key", "value");

            final var user = createUser();
            final var token = service.generateToken(user, hashMap);

            assertNotNull(token);
            assertTrue(token.matches("^[\\w-]+\\.[\\w-]+\\.[\\w-]+$"), "Returned value is not valid JWT");
            assertTrue(token.startsWith("eyJhbGciOiJIUzI1NiJ9."));
        }

        @Test
        @DisplayName("should throw exception if secret key is incorrect")
        public void generateToken4() {
            final var user = createUser();
            setField(service, "secretKey", "invalidSecretKey");
            assertThrows(
                    ResponseStatusException.class,
                    () -> service.generateToken(user, new HashMap<>())
            );
        }
    }

    @Test
    @DisplayName("isTokenValid() should return false if token does not match with user")
    public void isTokenValid2() {
        final var user = new User();
        user.setId(2);

        var isValid = service.isTokenValid(mockedToken, user);
        assertFalse(isValid);
    }

    @Test
    @DisplayName("extractUsername(token) should return username from token")
    public void extractUsername() {
        var username = service.extractUsername(mockedToken);
        assertEquals("1", username);
    }
}
