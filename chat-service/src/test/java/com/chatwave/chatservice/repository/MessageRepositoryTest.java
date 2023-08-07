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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DisplayName("MessageRepository")
public class MessageRepositoryTest {
    @MockBean
    private UserAuthFilter userAuthFilter;
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
    @DisplayName("findChat() should find all messages from chat if pageable is null")
    void t1() {
        var result = repository.findChat(1, 2, null);

        assertEquals(4, result.size());
        assertEquals(messages.get(3), result.get(0));
        assertEquals(messages.get(2), result.get(1));
        assertEquals(messages.get(1), result.get(2));
        assertEquals(messages.get(0), result.get(3));
    }

    @Test
    @DisplayName("findChat() should find proper message's page if we use pageable")
    void t2() {
        var pageable = Pageable.ofSize(2);
        var result = repository.findChat(1, 2, pageable.withPage(1));

        assertEquals(2, result.size());
        assertEquals(messages.get(1), result.get(0));
        assertEquals(messages.get(0), result.get(1));
    }
}
