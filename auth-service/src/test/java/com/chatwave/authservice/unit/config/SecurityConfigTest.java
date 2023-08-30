package com.chatwave.authservice.unit.config;

import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.domain.user.User;
import com.chatwave.authservice.repository.SessionRepository;
import com.chatwave.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityConfig")
public class SecurityConfigTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionRepository sessionRepository;
    private Session session;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        sessionRepository.deleteAll();

        var user = new User();
        user.setId(1);
        user.setPassword("pass");
        userRepository.save(user);

        session = new Session(user);
        sessionRepository.save(session);
    }

    @Test
    @DisplayName("test custom user's UserAuthFilter in integration test")
    @WithMockUser(authorities = "SCOPE_server")
    public void userAuthFilter() throws Exception {
        var result = mvc.perform(
                        get("/users/authentication")
                .header("User-Authorization","Bearer " + session.getAccessToken())
        ).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

        assertTrue(result.contains("\"principal\":1"));
    }
}
