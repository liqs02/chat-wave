package com.chatwave.chatservice.controller;

import com.chatwave.chatservice.domain.Message;
import com.chatwave.chatservice.domain.MessageMapper;
import com.chatwave.chatservice.domain.dto.GetMessagesRequest;
import com.chatwave.chatservice.domain.dto.MessageResponse;
import com.chatwave.chatservice.domain.dto.SendMessageRequest;
import com.chatwave.chatservice.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChatController")
public class ChatControllerTest {
    @InjectMocks
    private ChatController controller;
    @Mock
    private ChatService service;
    @Mock
    private MessageMapper mapper;

    Message message;
    MessageResponse messageResponse;

    @BeforeEach
    void setup() {
        message = new Message("Hello world!", 1, 2);
        messageResponse = new MessageResponse("Hello world!", 1, 2, LocalDateTime.MIN);
    }

    @Test
    @DisplayName("sendMessage() should save message and return messageResponse")
    void sendMessage() {
        var sendMessageRequest = new SendMessageRequest("Hello world!");

        when(
                mapper.toMessage(sendMessageRequest,1, 2)
        ).thenReturn(message);

        when(
                service.sendMessage(message)
        ).thenReturn(message);

        controller.sendMessage(sendMessageRequest, 1, 2);

        verify(
                service, times(1)
        ).sendMessage(message);
    }

    @Test
    @DisplayName("getMessages() should get and return messages by service")
    void getMessages() {
        var now = LocalDateTime.now();

        when(
                service.getMessages(1,2, now, true)
        ).thenReturn(List.of(message));

        when(
                mapper.toMessageResponse(message)
        ).thenReturn(messageResponse);

        var result = controller.getMessages(new GetMessagesRequest(now, true), 1,2);

        assertEquals(List.of(messageResponse), result);

        verify(
                service, times(1)
        ).getMessages(1,2, now, true);
    }
}
