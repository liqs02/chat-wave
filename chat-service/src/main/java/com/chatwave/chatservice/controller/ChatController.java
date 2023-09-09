package com.chatwave.chatservice.controller;

import com.chatwave.chatservice.domain.MessageMapper;
import com.chatwave.chatservice.domain.dto.GetMessagesRequest;
import com.chatwave.chatservice.domain.dto.MessageResponse;
import com.chatwave.chatservice.domain.dto.SendMessageRequest;
import com.chatwave.chatservice.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChatService service;
    private final MessageMapper mapper;

    @GetMapping("/{memberId}")
    public List<MessageResponse> getMessages(@Valid @RequestBody GetMessagesRequest getMessagesRequest, @AuthenticationPrincipal Integer authorId, @PathVariable Integer memberId) {
        return service
                .getMessages(authorId, memberId, getMessagesRequest.from(), getMessagesRequest.newer())
                .parallelStream()
                .map(mapper::toMessageResponse)
                .toList();
    }

    @PostMapping("/{receiverId}")
    public void sendMessage(@Valid @RequestBody SendMessageRequest sendMessageRequest, @AuthenticationPrincipal Integer authorId, @PathVariable Integer receiverId) {
        var message = mapper.toMessage(sendMessageRequest, authorId, receiverId);
        service.sendMessage(message);
    }
}
