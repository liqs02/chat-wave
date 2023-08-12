package com.chatwave.chatservice.domain;

import com.chatwave.chatservice.domain.dto.SendMessageRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("MessageMapper")
public class MessageMapperTest {
     MessageMapper mapper = MessageMapper.INSTANCE;

     @Test
     @DisplayName("should map SendMessageRequest and authorId to message entity")
     void t1() {
         var createMessageRequest = new SendMessageRequest(2, "It's a message.");
         var result = mapper.toMessage(createMessageRequest, 1);

         assertEquals("It's a message.", result.getContent());
         assertEquals(1, result.getAuthorId());
         assertEquals(2, result.getReceiverId());

         assertTrue(result.getId() != null);
         assertTrue(result.getCreatedAt() != null);
     }

    @Test
    @DisplayName("should map Message entity to MessageResponse")
    void t2() {
        var message = new Message("It's a message.", 1,2);
        var result = mapper.toMessageResponse(message);

        assertEquals("It's a message.", result.content());
        assertEquals(1, result.authorId());
        assertEquals(2, result.receiverId());
        assertEquals(message.getCreatedAt(), result.createdAt());
    }
}
