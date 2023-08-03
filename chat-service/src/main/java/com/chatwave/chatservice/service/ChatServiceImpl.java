package com.chatwave.chatservice.service;

import com.chatwave.chatservice.domain.Message;
import com.chatwave.chatservice.repository.MessageRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Setter(onMethod_=@Autowired)
@Slf4j
public class ChatServiceImpl implements ChatService{
    @Qualifier("chatPageable")
    private Pageable pageable;
    private MessageRepository repository;

    @Override
    public List<Message> getMessagePage(Integer firstMemberId, Integer secondMemberId, Integer page) {
        return repository.findChat(firstMemberId, secondMemberId, pageable.withPage(page));
    }

    @Override
    public Message sendMessage(Message message) {
        repository.save(message);
        return message;
    }
}
