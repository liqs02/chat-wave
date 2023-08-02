package com.chatwave.chatservice.domain;

import com.chatwave.chatservice.domain.dto.CreateMessageRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MessageMapper {
    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);
    @Mapping(source = "createMessageRequest.message", target = "content")
    Message toMessage(CreateMessageRequest createMessageRequest, Integer authorId);
}
