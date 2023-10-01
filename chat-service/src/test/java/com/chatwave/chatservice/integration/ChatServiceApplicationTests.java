package com.chatwave.chatservice.integration;

import com.chatwave.authclient.filter.UserAuthFilter;
import com.chatwave.chatservice.utils.ContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@Import(ContainersConfig.class)
@SpringBootTest
class ChatServiceApplicationTests {
    @MockBean
    private UserAuthFilter userAuthFilter;
    @Test
    void contextLoads() {
    }

}
