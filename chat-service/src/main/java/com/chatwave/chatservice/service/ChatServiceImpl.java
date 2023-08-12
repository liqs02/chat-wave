package com.chatwave.chatservice.service;

import com.chatwave.chatservice.domain.Message;
import com.chatwave.chatservice.repository.MessageRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Setter(onMethod_=@Autowired)
@Slf4j
public class ChatServiceImpl implements ChatService{
    private MessageRepository repository;

    @Override
    public List<Message> getMessages(Integer firstMemberId, Integer secondMemberId, LocalDateTime from) {
        return repository.findChat(firstMemberId, secondMemberId, from);
    }

    @Override
    public Message sendMessage(Message message) {
        repository.save(message);
        return message;
    }
}
