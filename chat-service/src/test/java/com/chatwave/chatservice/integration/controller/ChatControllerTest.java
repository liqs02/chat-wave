package com.chatwave.chatservice.integration.controller;

import com.chatwave.authclient.domain.UserAuthentication;
import com.chatwave.authclient.domain.UserAuthenticationDetails;
import com.chatwave.chatservice.client.AccountClient;
import com.chatwave.chatservice.client.AuthClient;
import com.chatwave.chatservice.domain.dto.SendMessageRequest;
import com.chatwave.chatservice.repository.ChatRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.chatwave.chatservice.utils.TestVariables.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@DisplayName("ChatController integration tests")
public class ChatControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private ChatRepository chatRepository;
    @MockBean
    private AuthClient authClient;
    @MockBean
    private AccountClient accountClient;

    @AfterEach
    void tearDown() {
        chatRepository.deleteAll();
    }

    private void mockAuthentication() {
        var userAuthentication = new UserAuthentication();
        userAuthentication.setUserId(USER_ID);
        userAuthentication.setDetails(new UserAuthenticationDetails());

        when(
                authClient.getUserAuthentication(BEARER_TOKEN)
        ).thenReturn(userAuthentication);
    }

    @Nested
    @DisplayName("POST /chat/{receiverId}")
    public class c2 {
        @Test
        @DisplayName("should send a message if account exist")
        public void t1() {
            mockAuthentication();

            webTestClient.post()
                    .uri("/chat/{receiverId}", RECEIVER_ID)
                    .header("User-Authorization", BEARER_TOKEN)
                    .bodyValue(new SendMessageRequest(MESSAGE_CONTENT))
                    .exchange()
                    .expectStatus().isOk();

            var messages = chatRepository.findAll();
            assertEquals(1, messages.size());

            var message = messages.get(0);
            assertEquals(USER_ID, message.getAuthorId());
            assertEquals(RECEIVER_ID, message.getReceiverId());
            assertEquals(MESSAGE_CONTENT, message.getContent());
            assertNotNull(message.getCreatedAt());
        }
}

















}
