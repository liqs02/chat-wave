package com.chatwave.chatservice.config;

import com.chatwave.authclient.filter.UserAuthFilter;
import com.chatwave.chatservice.domain.MessageMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    MessageMapper messageMapper() {
        return MessageMapper.INSTANCE;
    }

    @Bean
    UserAuthFilter userAuthFilter() {
        return new UserAuthFilter();
    }
}
