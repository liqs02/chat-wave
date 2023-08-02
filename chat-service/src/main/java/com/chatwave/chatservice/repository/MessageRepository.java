package com.chatwave.chatservice.repository;

import com.chatwave.chatservice.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {}
