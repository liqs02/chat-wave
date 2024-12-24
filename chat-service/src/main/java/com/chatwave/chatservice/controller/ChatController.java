package com.chatwave.chatservice.controller;

import com.chatwave.chatservice.domain.MessageMapper;
import com.chatwave.chatservice.domain.dto.MessageResponse;
import com.chatwave.chatservice.domain.dto.SendMessageRequest;
import com.chatwave.chatservice.service.ChatService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@RestController
@RequestMapping(value = "/chat", produces = APPLICATION_JSON)
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChatService service;
    private final MessageMapper mapper;

    @GetMapping("/{memberId}")
    public List<MessageResponse> getMessages(
            @NotNull @RequestParam("since") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since,
            @NotNull @RequestParam("newer") Boolean newer,
            @AuthenticationPrincipal Integer authorId,
            @PathVariable Integer memberId)
    {
        return service.getMessages(authorId, memberId, since, newer)
                .stream()
                .map(mapper::toMessageResponse)
                .toList();
    }

    @PostMapping(value = "/{receiverId}", consumes = APPLICATION_JSON)
    public void sendMessage(@Valid @RequestBody SendMessageRequest sendMessageRequest, @AuthenticationPrincipal Integer authorId, @PathVariable Integer receiverId) {
        var message = mapper.toMessage(sendMessageRequest, authorId, receiverId);
        service.sendMessage(message);
    }
}
