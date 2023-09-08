package com.chatwave.chatservice.service;

import com.chatwave.chatservice.client.AccountClient;
import com.chatwave.chatservice.domain.Message;
import com.chatwave.chatservice.repository.MessageRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService{
    private final MessageRepository repository;
    private final AccountClient accountClient;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Message> getMessages(Integer firstMemberId, Integer secondMemberId, LocalDateTime from, Boolean newer) {
        if(newer)
            return repository.findMessagesAfter(firstMemberId, secondMemberId, from);
        else
            return repository.findMessagesBefore(firstMemberId, secondMemberId, from);
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
