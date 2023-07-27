package com.chatwave.accountservice.config;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
@Setter(onMethod_=@Autowired)
public class SecurityConfig {
    UserAuthFilter userAuthFilter;

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth ->
                    auth.requestMatchers(GET, "/actuator/health").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(POST, "/accounts", "/accounts/authenticate").permitAll()
                        .anyRequest().authenticated()
            )
            .addFilterBefore(userAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
