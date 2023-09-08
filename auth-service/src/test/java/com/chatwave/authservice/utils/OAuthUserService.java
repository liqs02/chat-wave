package com.chatwave.authservice.utils;

import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.domain.user.User;
import com.chatwave.authservice.repository.SessionRepository;
import com.chatwave.authservice.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.chatwave.authservice.utils.TestVariables.LOGIN_NAME;
import static com.chatwave.authservice.utils.TestVariables.PASSWORD;

@RequiredArgsConstructor
public class OAuthUserService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;

    @Getter
    private Session session;
    @Getter
    private Integer userId;

    public void createUserAndSession() {
        var encoded = passwordEncoder.encode(PASSWORD);

        var user = new User();
        user.setLoginName(LOGIN_NAME);
        user.setPassword(encoded);

        userRepository.save(user);
        userId = user.getId();

        session = new Session(user);
        sessionRepository.save(session);
    }

    public void cleanDatabase() {
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
