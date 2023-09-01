package com.chatwave.accountservice.config;

import com.chatwave.accountservice.client.AuthClient;
import com.chatwave.accountservice.domain.AccountMapper;
import com.chatwave.authclient.filter.UserAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    AccountMapper accountMapper() {
        return AccountMapper.INSTANCE;
    }

    @Bean
    UserAuthFilter userAuthFilter(@Autowired AuthClient authService) {
        return new UserAuthFilter(authService);
    }

    @Bean
    public String activeProfile(@Value("${spring.profiles.active}") String activeProfile) {
        return activeProfile;
    }
}
