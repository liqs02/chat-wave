package com.chatwave.chatservice.config;

import com.chatwave.authclient.filter.UserAuthFilter;
import com.chatwave.chatservice.client.AuthClient;
import com.chatwave.chatservice.domain.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AppConfig {
    @Bean
    public MessageMapper messageMapper() {
        return MessageMapper.INSTANCE;
    }

    @Bean
    public UserAuthFilter userAuthFilter(@Autowired AuthClient authClient) {
        return new UserAuthFilter(authClient);
    }

    @Bean
    public List<String> activeProfiles(@Value("${spring.profiles.active}") List<String> activeProfiles) {
        return activeProfiles;
    }
}
