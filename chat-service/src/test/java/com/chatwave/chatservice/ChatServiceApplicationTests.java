package com.chatwave.chatservice;

import com.chatwave.authclient.filter.UserAuthFilter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@SpringBootTest
class ChatServiceApplicationTests {
    @MockBean
    private UserAuthFilter userAuthFilter;
    @MockBean
    private SimpMessagingTemplate messagingTemplate;
    @Test
    void contextLoads() {
    }

}
