package com.chatwave.chatservice.controller;

import com.chatwave.chatservice.domain.MessageMapper;
import com.chatwave.chatservice.domain.dto.GetMessagesRequest;
import com.chatwave.chatservice.domain.dto.MessageResponse;
import com.chatwave.chatservice.domain.dto.SendMessageRequest;
import com.chatwave.chatservice.service.ChatService;
import jakarta.validation.Valid;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping
@Setter(onMethod_=@Autowired)
public class ChatController {
    private ChatService service;
    private MessageMapper mapper;
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/chat")
    public List<MessageResponse> getMessages(@AuthenticationPrincipal Integer authorId, @Valid @RequestBody GetMessagesRequest getMessagesRequest) {
        var messages = service.getMessages(authorId, getMessagesRequest.receiverId(), getMessagesRequest.from());
        return messages
                .stream()
                .map(message -> mapper.toMessageResponse(message))
                .collect(Collectors.toList());
    }

    @MessageMapping("/chat")
    public Integer sendMessage(@Valid @Payload SendMessageRequest sendMessageRequest, @AuthenticationPrincipal Integer authorId) {
        var message = mapper.toMessage(sendMessageRequest, authorId);
        message = service.sendMessage(message);

        messagingTemplate.convertAndSendToUser(sendMessageRequest.receiverId().toString(), "/topic/notifications", message);
        return sendMessageRequest.receiverId();
    }
}
