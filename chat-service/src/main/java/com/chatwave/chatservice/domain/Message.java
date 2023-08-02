package com.chatwave.chatservice.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(
        name = "messages",
        indexes = {
                @Index(name = "mess_author_id_index", columnList = "authorId"),
                @Index(name = "mess_receiver_id_index", columnList = "receiverId"),
                @Index(name = "mess_created_at_index", columnList = "createdAt")
        }
)
public class Message {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, updatable = false)
    private Integer authorId;
    @Column(nullable = false, updatable = false)
    private Integer receiverId;
    @Column(nullable = false, updatable = false, length = 2000)
    private String content;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    public Message(String content, Integer authorId, Integer receiverId) {
        this.content = content;
        this.authorId = authorId;
        this.receiverId = receiverId;
    }
}
