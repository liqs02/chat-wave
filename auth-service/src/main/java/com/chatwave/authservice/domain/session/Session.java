package com.chatwave.authservice.domain.session;

import com.chatwave.authservice.domain.user.User;
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
    @JoinColumn(nullable=false, updatable = false)
    private User user;

    @Column(unique = true)
    private String accessToken;

    @Column(nullable = false)
    private LocalDateTime accessTokenExpireDate; // The dateTime from which accessToken is expired and have to be refreshed.

    @Column(unique = true)
    private String refreshToken;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate expireDate; // The date from which accessToken and refreshToken is expired.

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

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
        return now.isAfter(accessTokenExpireDate) || isExpired();
    }

     public void expire() {
         if(!isExpired())
            expireDate = LocalDate.now();

        accessToken = null;
        refreshToken = null;
    }

    public void refreshTokens() {
        accessToken = generateToken(192);
        accessTokenExpireDate = LocalDateTime.now().plusHours(1);

        refreshToken = generateToken(255);
        expireDate = LocalDate.now().plusMonths(3);
    }

    private String generateToken(int count) {
        return RandomStringUtils.random(count, 0, 0, true, true, null, new SecureRandom());
    }
}
