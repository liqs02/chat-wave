package com.chatwave.accountservice.config;

import com.chatwave.accountservice.client.AuthClient;
import com.chatwave.accountservice.domain.AccountMapper;
import com.chatwave.authclient.filter.UserAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AppConfig {
    @Bean
    public List<String> activeProfiles(@Value("${spring.profiles.active}") List<String> activeProfiles) {
        return activeProfiles;
    }

    @Bean
    public AccountMapper accountMapper() {
        return AccountMapper.INSTANCE;
    }

    @Bean
    public UserAuthFilter userAuthFilter(@Autowired AuthClient authService) {
        return new UserAuthFilter(authService);
    }
}
