package com.chatwave.accountservice.integration;

import com.chatwave.accountservice.utils.ContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(ContainersConfig.class)
@SpringBootTest
class AccountServiceApplicationTests {
    @Test
    void contextLoads() {
    }
}
