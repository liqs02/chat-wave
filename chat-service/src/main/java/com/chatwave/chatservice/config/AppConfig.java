package com.chatwave.chatservice.config;

import com.chatwave.chatservice.domain.MessageMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;

@Configuration
public class AppConfig {
    @Bean
    MessageMapper messageMapper() {
        return MessageMapper.INSTANCE;
    }

    @Bean
    Pageable chatPageable() {
        return Pageable.ofSize(10);
    }
}
