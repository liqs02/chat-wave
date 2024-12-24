package com.chatwave.chatservice.integration.repository;

import com.chatwave.chatservice.domain.Message;
import com.chatwave.chatservice.repository.ChatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static com.chatwave.chatservice.utils.TestVariables.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DisplayName("ChatRepository")
public class ChatRepositoryTest {
    @Autowired
    private ChatRepository repository;
    private List<Message> messages;

    @BeforeEach
    void setup() {
        repository.deleteAll();

        messages = List.of(
                // chats between 1st and 2nd user
                new Message(MESSAGE_CONTENT + "1", USER_ID, RECEIVER_ID),
                new Message(MESSAGE_CONTENT + "2", RECEIVER_ID, USER_ID),
                new Message(MESSAGE_CONTENT + "3", USER_ID, RECEIVER_ID),
                new Message(MESSAGE_CONTENT + "4", RECEIVER_ID, USER_ID),
                // other chats
                new Message(MESSAGE_CONTENT + "5", USER_ID, 3),
                new Message(MESSAGE_CONTENT + "6", RECEIVER_ID, 3),
                new Message(MESSAGE_CONTENT + "7", 3, USER_ID),
                new Message(MESSAGE_CONTENT + "8", 3, RECEIVER_ID)
        );

        repository.saveAll(messages);
    }

    @Test
    @DisplayName("findMessagesBefore() should find all messages from chat")
    void t1() {
        var result = repository.findMessagesBefore(USER_ID, RECEIVER_ID, LocalDateTime.now());

        assertEquals(4, result.size());
        assertEquals(messages.get(3), result.get(0));
        assertEquals(messages.get(2), result.get(1));
        assertEquals(messages.get(1), result.get(2));
        assertEquals(messages.get(0), result.get(3));
    }

    @Test
    @DisplayName("findMessagesBefore() shouldn't find any messages from chat")
    void t2() {
        var result = repository.findMessagesBefore(USER_ID, RECEIVER_ID, LocalDateTime.now().minusHours(1));
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("findMessagesAfter() should find all messages from chat")
    void t3() {
        var result = repository.findMessagesAfter(USER_ID, RECEIVER_ID, LocalDateTime.now().minusHours(1));

        assertEquals(4, result.size());
        assertEquals(messages.get(0), result.get(0));
        assertEquals(messages.get(1), result.get(1));
        assertEquals(messages.get(2), result.get(2));
        assertEquals(messages.get(3), result.get(3));
    }

    @Test
    @DisplayName("findMessagesAfter() shouldn't find any messages from chat")
    void t4() {
        var result = repository.findMessagesAfter(USER_ID, RECEIVER_ID, LocalDateTime.now());
        assertEquals(0, result.size());
    }

}
