package com.chatwave.authservice.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDate;
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
    private LocalDate expirationAt; // TODO: add scheduling to delete entities if is expired

    public RefreshToken() {
        this.expirationAt = LocalDate.now().plusMonths(1);
    }
}
