package com.chatwave.chatservice.integration.controller;

import com.chatwave.authclient.domain.UserAuthentication;
import com.chatwave.authclient.domain.UserAuthenticationDetails;
import com.chatwave.chatservice.domain.Message;
import com.chatwave.chatservice.domain.dto.MessageResponse;
import com.chatwave.chatservice.domain.dto.SendMessageRequest;
import com.chatwave.chatservice.repository.ChatRepository;
import com.chatwave.chatservice.utils.ContainersConfig;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;

import static com.chatwave.chatservice.utils.JsonUtils.toJson;
import static com.chatwave.chatservice.utils.TestVariables.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Import(ContainersConfig.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@DisplayName("ChatController integration tests")
public class ChatControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private ChatRepository chatRepository;
    private static WireMockServer mockAuthService;
    private static WireMockServer mockAccountService;
    @BeforeAll
    static void startWireMock() {
        mockAuthService = new WireMockServer(
                WireMockConfiguration.wireMockConfig().dynamicPort()
        );
        mockAuthService.start();

        mockAccountService = new WireMockServer(
                WireMockConfiguration.wireMockConfig().dynamicPort()
        );
        mockAccountService.start();
    }

    @DynamicPropertySource
    public static void overrideWebClientBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("auth-service.url", mockAuthService::baseUrl);
        registry.add("account-service.url", mockAccountService::baseUrl);
    }

    @AfterAll
    public static void stopWireMock() {
        mockAuthService.stop();
    }

    @BeforeEach
    public void setUp() {
        var userAuthentication = new UserAuthentication();
        userAuthentication.setUserId(USER_ID);
        userAuthentication.setDetails(new UserAuthenticationDetails());

        mockAuthService.stubFor(
                get("/sessions/authentication")
                        .withHeader("User-Authorization", equalTo(BEARER_TOKEN))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", APPLICATION_JSON)
                                        .withBody(toJson(userAuthentication)))
        );
    }

    @Nested
    @DisplayName("GET /chat/{userId}")
    public class c1 {
        private LocalDateTime createdAt;
        private final Integer GET_MESSAGE_TIME_DELAY = 1000; // in nanos

        @BeforeEach
        public void setUp() {
            var message = new Message();
            message.setAuthorId(USER_ID);
            message.setReceiverId(RECEIVER_ID);
            message.setContent(MESSAGE_CONTENT);
            chatRepository.save(message);
            createdAt = message.getCreatedAt();
        }

        @AfterEach
        public void tearDown() {
            chatRepository.deleteAll();
        }

        private void verifyMessageResponse(MessageResponse message) {
            assertEquals(USER_ID, message.authorId());
            assertEquals(RECEIVER_ID, message.receiverId());
            assertEquals(MESSAGE_CONTENT, message.content());
            assertNotNull(message.createdAt());
        }

        @Test
        @DisplayName("should get newer message than provided date")
        public void t1() {
            var messages = webTestClient.get()
                    .uri(builder ->
                            builder.path("/chat/" + RECEIVER_ID)
                                    .queryParam("since", createdAt.minusNanos(GET_MESSAGE_TIME_DELAY))
                                    .queryParam("newer", true)
                                    .build()
                    )
                    .header("User-Authorization", BEARER_TOKEN)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(MessageResponse.class).returnResult().getResponseBody();

            assertNotNull(messages);
            assertEquals(1, messages.size());

            verifyMessageResponse(messages.get(0));
        }

        @Test
        @DisplayName("should get older message than provided date")
        public void t2() {
            var messages = webTestClient.get()
                    .uri(builder ->
                            builder.path("/chat/" + RECEIVER_ID)
                                    .queryParam("since", createdAt.plusNanos(GET_MESSAGE_TIME_DELAY))
                                    .queryParam("newer", false)
                                    .build()
                    )
                    .header("User-Authorization", BEARER_TOKEN)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(MessageResponse.class).returnResult().getResponseBody();


            assertNotNull(messages);
            assertEquals(1, messages.size());

            verifyMessageResponse(messages.get(0));
        }

        @Test
        @DisplayName("should do return empty array if there are not messages")
        public void t3() {
            var messages = webTestClient.get()
                    .uri(builder ->
                            builder.path("/chat/" + RECEIVER_ID)
                                    .queryParam("since", createdAt.minusNanos(GET_MESSAGE_TIME_DELAY))
                                    .queryParam("newer", false)
                                    .build()
                    )
                    .header("User-Authorization", BEARER_TOKEN)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(MessageResponse.class).returnResult().getResponseBody();


            assertNotNull(messages);
            assertEquals(0, messages.size());
        }

        @Test
        @DisplayName("should do return empty array if there are not messages")
        public void t4() {
            var messages = webTestClient.get()
                    .uri(builder ->
                            builder.path("/chat/" + RECEIVER_ID)
                                    .queryParam("since", createdAt.plusNanos(GET_MESSAGE_TIME_DELAY))
                                    .queryParam("newer", true)
                                    .build()
                    )
                    .header("User-Authorization", BEARER_TOKEN)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(MessageResponse.class).returnResult().getResponseBody();

            assertNotNull(messages);
            assertEquals(0, messages.size());
        }
    }

    @Nested
    @DisplayName("POST /chat/{receiverId}")
    public class c2 {
        @AfterEach
        void tearDown() {
            chatRepository.deleteAll();
        }

        @Test
        @DisplayName("should send a message if user exist")
        public void t1() {
            mockAccountService.stubFor(
                    get("/accounts/" + RECEIVER_ID + "/exist").willReturn(aResponse().withStatus(200))
            );

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

        @Test
        @DisplayName("should send a message if user wants send message to himself")
        public void t2() {
            mockAccountService.stubFor(
                    get("/accounts/" + USER_ID + "/exist").willReturn(aResponse().withStatus(200))
            );

            webTestClient.post()
                    .uri("/chat/{receiverId}", USER_ID)
                    .header("User-Authorization", BEARER_TOKEN)
                    .bodyValue(new SendMessageRequest(MESSAGE_CONTENT))
                    .exchange()
                    .expectStatus().isOk();

            var messages = chatRepository.findAll();
            assertEquals(1, messages.size());

            var message = messages.get(0);
            assertEquals(USER_ID, message.getAuthorId());
            assertEquals(USER_ID, message.getReceiverId());
            assertEquals(MESSAGE_CONTENT, message.getContent());
            assertNotNull(message.getCreatedAt());
        }

        @Test
        @DisplayName("should throw BAD_REQUEST if user with given ID does not exist in account-service")
        public void t3() {
            mockAccountService.stubFor(
                    get("/accounts/" + USER_ID + "/exist").willReturn(aResponse().withStatus(404))
            );

            webTestClient.post()
                    .uri("/chat/{receiverId}", USER_ID)
                    .header("User-Authorization", BEARER_TOKEN)
                    .bodyValue(new SendMessageRequest(MESSAGE_CONTENT))
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }
}
