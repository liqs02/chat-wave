package com.chatwave.chatservice.controller;

import com.chatwave.chatservice.domain.MessageMapper;
import com.chatwave.chatservice.domain.dto.GetMessagesRequest;
import com.chatwave.chatservice.domain.dto.MessageResponse;
import com.chatwave.chatservice.domain.dto.SendMessageRequest;
import com.chatwave.chatservice.service.ChatService;
import jakarta.validation.Valid;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/chat")
@Setter(onMethod_=@Autowired)
public class ChatController {
    private ChatService service;
    private MessageMapper mapper;

    @GetMapping("/{receiverId}")
    public List<MessageResponse> getMessages(@Valid @RequestBody GetMessagesRequest getMessagesRequest, @AuthenticationPrincipal Integer authorId, @PathVariable Integer receiverId) {
        return service
                .getMessages(authorId, receiverId, getMessagesRequest.from(), getMessagesRequest.newer())
                .parallelStream()
                .map(message -> mapper.toMessageResponse(message))
                .collect(Collectors.toList());
    }

    @PostMapping("/{receiverId}")
    public void sendMessage(@Valid @RequestBody SendMessageRequest sendMessageRequest, @AuthenticationPrincipal Integer authorId, @PathVariable Integer receiverId) {
        var message = mapper.toMessage(sendMessageRequest, authorId, receiverId);
        service.sendMessage(message);
    }
}
