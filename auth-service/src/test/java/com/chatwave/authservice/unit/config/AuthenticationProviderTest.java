package com.chatwave.authservice.unit.config;

import com.chatwave.authservice.domain.user.User;
import com.chatwave.authservice.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DisplayName("AppConfig")
public class AuthenticationProviderTest {
    @Autowired
    AuthenticationProvider authenticationProvider;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("AuthenticationProvider test")
    public void AuthenticationProvider() {
        var user = new User();
        user.setId(1);

        var encoded = passwordEncoder.encode("pass");

        user.setPassword(encoded);

        userRepository.save(user);

        var result = authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(1, "pass")
        );


        var principal = (User) result.getPrincipal();
        assertEquals(1, principal.getId());
        assertEquals(encoded, principal.getPassword());

        assertEquals("pass", result.getCredentials());
    }
}
