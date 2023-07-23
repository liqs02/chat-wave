package com.chatwave.accountservice;

import com.chatwave.accountservice.client.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class AccountServiceApplicationTests {
    @MockBean
    AuthService authService;

    @Test
    void contextLoads() {
    }

}
