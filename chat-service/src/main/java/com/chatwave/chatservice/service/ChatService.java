package com.chatwave.chatservice.service;

import com.chatwave.chatservice.domain.Message;

import java.util.List;

public interface ChatService {
    /**
     * Searches messages between first and second member.
     * Sorts messages by date (descending order).
     * Skips page * size messages.
     *
     * @param firstMemberId - first chat's memberId
     * @param secondMemberId - second chat's memberId
     * @param page index
     * @return message list with fixed size
     */
    List<Message> getMessagePage(Integer firstMemberId, Integer secondMemberId, Integer page);


    /**
     * Saves message in database.
     *
     * @param message
     * @return message
     */
    Message sendMessage(Message message);
}
