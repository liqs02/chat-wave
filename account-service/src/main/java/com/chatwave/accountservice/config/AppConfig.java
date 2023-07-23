package com.chatwave.accountservice.config;

import com.chatwave.accountservice.domain.AccountMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    AccountMapper accountMapper() {
        return AccountMapper.INSTANCE;
    }
}
