package com.chatwave.accountservice.integration;

import com.chatwave.accountservice.client.AuthClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class AccountServiceApplicationTests {
    @MockBean
    AuthClient authService;

    @Test
    void contextLoads() {
    }

}
