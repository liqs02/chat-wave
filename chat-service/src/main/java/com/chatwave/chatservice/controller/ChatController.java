package com.chatwave.chatservice.controller;

import com.chatwave.chatservice.domain.MessageMapper;
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
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/chat/{receiverId}/{page}")
    public List<MessageResponse> getMessagePage(@AuthenticationPrincipal Integer authorId, @PathVariable Integer receiverId, @PathVariable Integer page) {
        var messages = service.getMessagePage(authorId, receiverId, page);
        return messages
                .stream()
                .map(message -> mapper.toMessageResponse(message))
                .collect(Collectors.toList());
    }

    @MessageMapping("/chat/{receiverId}")
    public MessageResponse sendMessage(@Valid @Payload SendMessageRequest sendMessageRequest, @AuthenticationPrincipal Integer authorId, @DestinationVariable Integer receiverId) {
        var message = mapper.toMessage(sendMessageRequest, authorId, receiverId);
        message = service.sendMessage(message);

        messagingTemplate.convertAndSendToUser(receiverId.toString(), "/queue/" + receiverId, message);
        return mapper.toMessageResponse(message);
    }
}
