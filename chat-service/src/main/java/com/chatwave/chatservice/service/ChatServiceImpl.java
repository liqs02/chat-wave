package com.chatwave.chatservice.service;

import com.chatwave.chatservice.client.AccountClient;
import com.chatwave.chatservice.domain.Message;
import com.chatwave.chatservice.repository.ChatRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRepository repository;
    private final AccountClient accountClient;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Message> getMessages(Integer firstMemberId, Integer secondMemberId, LocalDateTime since, Boolean newer) {
        if(newer)
            return repository.findMessagesAfter(firstMemberId, secondMemberId, since);
        else
            return repository.findMessagesBefore(firstMemberId, secondMemberId, since);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message sendMessage(Message message) {
        try {
            accountClient.doesAccountExist(message.getReceiverId());
        } catch(FeignException.NotFound e) {
            throw new ResponseStatusException(BAD_REQUEST, "ReceiverId is invalid, user with this ID does not exist");
        }
        repository.save(message);
        return message;
    }
}
