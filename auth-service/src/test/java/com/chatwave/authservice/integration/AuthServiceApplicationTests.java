package com.chatwave.authservice.integration;

import com.chatwave.authservice.utils.ContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(ContainersConfig.class)
@SpringBootTest
class AuthServiceApplicationTests {
    @Test
    void contextLoads() {
    }
}
