package com.chatwave.chatservice.config;

import com.chatwave.authclient.filter.UserAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final List<String> activeProfiles;
    private final UserAuthFilter userAuthFilter;

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth ->
                    auth.requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()
            )
            .oauth2ResourceServer(resourceServer ->
                    resourceServer.jwt(withDefaults())
            )
            .sessionManagement((session) ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterAt(userAuthFilter, UsernamePasswordAuthenticationFilter.class);

        if(activeProfiles.contains("csrf_disable"))
            http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
