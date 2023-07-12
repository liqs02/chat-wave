package com.chatwave.authservice.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.NONE;

@Data
@Entity
@Table( name = "refresh_tokens",
    indexes = {
        @Index(name = "refresh_token_user_index", columnList = "id"),
        @Index(name = "refresh_token_exp_at_index", columnList = "expirationAt")
    }
)
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false, updatable = false, columnDefinition = "DATE")
    @Setter(NONE)
    private LocalDate expirationAt;

    @Column(nullable = false, updatable = false)
    @Setter(NONE)
    private LocalDateTime createdAt;

    public RefreshToken() {
        this.expirationAt = LocalDate.now().plusMonths(1);
        this.createdAt = LocalDateTime.now();
    }

    /**
     * @return boolean represents that token is ready for refresh.
     */
    public boolean isActive() {
        var now = LocalDateTime.now();
        var limit = createdAt.plusMinutes(2); // equal to jwt expiration time
        return now.isAfter(limit);
    }
}
