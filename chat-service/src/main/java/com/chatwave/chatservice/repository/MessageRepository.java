package com.chatwave.chatservice.repository;

import com.chatwave.chatservice.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    @Query("SELECT m FROM Message m WHERE (m.authorId = ?1 AND m.receiverId = ?2) OR (m.authorId = ?2 AND m.receiverId = ?1) AND m.createdAt < ?3 ORDER BY m.createdAt ASC LIMIT 10")
    List<Message> findMessagesBefore(Integer firstMemberId, Integer secondMemberId, LocalDateTime from);

    @Query("SELECT m FROM Message m WHERE (m.authorId = ?1 AND m.receiverId = ?2) OR (m.authorId = ?2 AND m.receiverId = ?1) AND m.createdAt > ?3 ORDER BY m.createdAt ASC LIMIT 10")
    List<Message> findMessagesAfter(Integer firstMemberId, Integer secondMemberId, LocalDateTime from);
}
