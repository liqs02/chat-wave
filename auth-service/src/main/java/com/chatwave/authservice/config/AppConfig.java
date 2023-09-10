package com.chatwave.authservice.config;

import com.chatwave.authservice.domain.Client;
import com.chatwave.authservice.domain.session.SessionMapper;
import com.chatwave.authservice.domain.user.UserMapper;
import com.chatwave.authservice.repository.UserRepository;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Setter
@Configuration
@EnableConfigurationProperties
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

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return loginName -> userRepository.findByLoginName(loginName)
                .orElseThrow(() ->  new UsernameNotFoundException("User with given loginName does not exist."));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
