package com.chatwave.chatservice.domain;

import com.chatwave.chatservice.domain.dto.CreateMessageRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("MessageMapper")
public class MessageMapperTest {
     MessageMapper mapper = MessageMapper.INSTANCE;

     @Test
     @DisplayName("should map CreateMessageRequest and authorId to message entity")
     void t1() {
         var createMessageRequest = new CreateMessageRequest(1, "It's a message.");
         var result = mapper.toMessage(createMessageRequest, 2);

         assertEquals("It's a message.", result.getContent());
         assertEquals(1, result.getReceiverId());
         assertEquals(2, result.getAuthorId());
     }
}
