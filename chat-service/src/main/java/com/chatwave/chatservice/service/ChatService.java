package com.chatwave.chatservice.service;

import com.chatwave.chatservice.domain.Message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ChatService {
    /**
     * Searches messages between first and second member.
     * Sorts messages by date (descending order).
     * Skips page * size messages.
     *
     * @param firstMemberId - first chat's memberId
     * @param secondMemberId - second chat's memberId
     * @param from datetime from which we should take a page (excluding this message with exactly this datetime)
     * @return message list with fixed size
     */
    List<Message> getMessages(Integer firstMemberId, Integer secondMemberId, LocalDateTime from);

    /**
     * Saves message in database.
     *
     * @param message
     * @return message
     */
    Message sendMessage(Message message);
}
