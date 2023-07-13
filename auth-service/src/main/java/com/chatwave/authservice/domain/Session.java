package com.chatwave.authservice.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.apache.commons.lang.RandomStringUtils;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table( name = "sessions",
    indexes = {
        @Index(name = "session_user_index", columnList = "user_id"),
            @Index(name = "session_access_token_index", columnList = "accessToken"),
        @Index(name = "session_refresh_token_index", columnList = "refreshToken")
    }
)
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String accessToken;

    @Column(nullable = false)
    private LocalDateTime accessTokenExpireDate;  // then we have to refresh token.

    @Column(nullable = false, unique = true)
    private String refreshToken;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate expireDate; // then we can not use access and refresh token!

    @Column(nullable = false, updatable = false)
    private final LocalDateTime createdAt;

    public Session() {
        this.createdAt = LocalDateTime.now();
        refreshTokens();
    }

    public Session(User user) {
        this();
        this.user = user;
    }

    public boolean isExpired() {
        var today = LocalDate.now();
        return today.isAfter(expireDate) || today.equals(expireDate);
    }

    public boolean isAccessTokenExpired() {
        var now = LocalDateTime.now();
        return now.isAfter(accessTokenExpireDate);
    }

    public void deactivate() {
        expireDate = LocalDate.now();
        accessTokenExpireDate = LocalDateTime.now();
    }

    public void refreshTokens() {
        accessToken = generateToken();
        accessTokenExpireDate = LocalDateTime.now().plusHours(1);

        refreshToken = generateToken();
        expireDate = LocalDate.now().plusMonths(3);
    }

    private String generateToken() {
        return RandomStringUtils.random(128, 0, 0, true, true, null, new SecureRandom());
    }
}
