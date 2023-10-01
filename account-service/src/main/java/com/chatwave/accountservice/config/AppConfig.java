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
    public Boolean csrfEnabled(@Value("${CSRF_ENABLED:true}") Boolean csrfEnabled) {
        return csrfEnabled;
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
