package com.chatwave.chatservice.config;

import com.chatwave.authclient.filter.UserAuthFilter;
import com.chatwave.chatservice.client.AuthClient;
import com.chatwave.chatservice.domain.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.Objects.requireNonNullElse;

@Configuration
public class AppConfig {
    @Bean
    MessageMapper messageMapper() {
        return MessageMapper.INSTANCE;
    }

    @Bean
    UserAuthFilter userAuthFilter(@Autowired AuthClient authClient) {
        return new UserAuthFilter(authClient);
    }

    @Bean
    public String activeProfile(@Value("${spring.profiles.active}") String activeProfile) {
        return requireNonNullElse(activeProfile, "");
    }
}
