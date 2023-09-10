package com.chatwave.authservice.config;

import com.chatwave.authservice.domain.Client;
import com.chatwave.authservice.domain.session.SessionMapper;
import com.chatwave.authservice.domain.user.UserMapper;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Setter
@Configuration
@ConfigurationProperties(prefix  = "app")
public class AppConfig {
    private List<Client> clients;

    @Bean
    public List<Client> clients() {
        return clients;
    }

    @Bean
    public List<String> activeProfile(@Value("${spring.profiles.active}") List<String> activeProfile) {
        return activeProfile;
    }

    @Bean
    public UserMapper userMapper() {
        return UserMapper.INSTANCE;
    }

    @Bean
    public SessionMapper sessionMapper() {
        return SessionMapper.INSTANCE;
    }
}
