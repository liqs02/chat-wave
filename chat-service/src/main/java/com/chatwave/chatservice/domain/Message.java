package com.chatwave.chatservice.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(
        name = "messages",
        indexes = {
                @Index(name = "messAuthorIdIndex", columnList = "authorId"),
                @Index(name = "messReceiverIdIndex", columnList = "receiverId"),
                @Index(name = "messCreatedAtIndex", columnList = "createdAt")
        }
)
public class Message {
    @Id @GeneratedValue
    private UUID id;
    @Column(nullable = false, updatable = false)
    private Integer authorId;
    @Column(nullable = false, updatable = false)
    private Integer receiverId;
    @Column(nullable = false, updatable = false, length = 2000)
    private String content;
    @Column(nullable = false, updatable = false)
    private final LocalDateTime createdAt;

    public Message() {
        this.createdAt = LocalDateTime.now();
    }

    public Message(String content, Integer authorId, Integer receiverId) {
        this();
        this.content = content;
        this.authorId = authorId;
        this.receiverId = receiverId;
    }
}
