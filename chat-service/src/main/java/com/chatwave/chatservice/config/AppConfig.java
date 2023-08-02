package com.chatwave.chatservice.config;

import com.chatwave.chatservice.domain.MessageMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    MessageMapper messageMapper() {
        return MessageMapper.INSTANCE;
    }
}
