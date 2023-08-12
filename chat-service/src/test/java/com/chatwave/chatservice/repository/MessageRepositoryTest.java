package com.chatwave.chatservice.repository;

import com.chatwave.authclient.filter.UserAuthFilter;
import com.chatwave.chatservice.domain.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DisplayName("MessageRepository")
public class MessageRepositoryTest {
    @MockBean
    private UserAuthFilter userAuthFilter;
    @MockBean
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private MessageRepository repository;
    private List<Message> messages;

    @BeforeEach
    void setup() {
        repository.deleteAll();

        messages = List.of(
                // chats between 1st and 2nd user
                new Message("Content 1", 1, 2),
                new Message("Content 2", 2, 1),
                new Message("Content 3", 1, 2),
                new Message("Content 4", 2, 1),
                // other chats
                new Message("Content 5", 1, 3),
                new Message("Content 6", 2, 3),
                new Message("Content 7", 3, 1),
                new Message("Content 8", 3, 2)
        );

        repository.saveAll(messages);
    }

    @Test
    @DisplayName("findChat() should find all messages from chat")
    void t1() {
        var result = repository.findChat(1, 2, LocalDateTime.now());

        assertEquals(4, result.size());
        assertEquals(messages.get(3), result.get(0));
        assertEquals(messages.get(2), result.get(1));
        assertEquals(messages.get(1), result.get(2));
        assertEquals(messages.get(0), result.get(3));
    }
}
