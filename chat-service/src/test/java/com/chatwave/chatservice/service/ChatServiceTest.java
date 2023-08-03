package com.chatwave.chatservice.service;

import com.chatwave.chatservice.domain.Message;
import com.chatwave.chatservice.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChatService")
public class ChatServiceTest {
    @InjectMocks
    ChatServiceImpl service;
    @Mock
    private MessageRepository repository;
    @Mock
    private Pageable pageable;

    private Message message;

    @BeforeEach
    void setup() {
        message = new Message();
    }

    @Test
    @DisplayName("getMessagePage() should get passed page by repository")
    void getMessagePage() {
        when(
                repository.findChat(1,2, pageable.withPage(3))
        ).thenReturn(List.of(message));

        var result = service.getMessagePage(1, 2, 3);

        assertEquals(List.of(message), result);
    }

    @Test
    @DisplayName("sendMessage() should save and return message")
    void sendMessage() {
        var result = service.sendMessage(message);

        verify(
                repository, times(1)
        ).save(message);

        assertEquals(message, result);
    }

}
