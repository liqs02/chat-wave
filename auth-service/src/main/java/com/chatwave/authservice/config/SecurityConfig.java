package com.chatwave.authservice.config;

import com.chatwave.authservice.domain.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;
import java.util.UUID;

import static jakarta.ws.rs.HttpMethod.POST;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final List<String> activeProfiles;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationProvider authenticationProvider;
    private final UserAuthFilter userAuthFilter;

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http.sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf ->
                        csrf.ignoringRequestMatchers("/users/**", "/sessions")
            )
            .authorizeHttpRequests(auth ->
                    auth.requestMatchers(GET, "/actuator/health").permitAll()
                            .requestMatchers(POST, "/sessions/refresh").permitAll()
                            .requestMatchers("/error").permitAll()
                            .anyRequest().authenticated()
            )
            .oauth2ResourceServer(resourceServer ->
                    resourceServer.jwt(withDefaults())
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(userAuthFilter, UsernamePasswordAuthenticationFilter.class);

        if(activeProfiles.contains("CSRF_DISABLE"))
            http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        var clients = List.of(
                new Client("account-service", "secret", "http://account-service:8082"),
                new Client("chat-service", "secret", "http://chat-service:8083")
        ); // todo: move this to application.yml

        var registeredClients = clients.parallelStream()
                .map(client ->
                        RegisteredClient.withId(UUID.randomUUID().toString())
                                .clientId(client.getId())
                                .clientSecret(passwordEncoder.encode(client.getSecret()))
                                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                                .redirectUri(client.getUrl())
                                .scope(OidcScopes.OPENID)
                                .scope("server")
                                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                                .build()
                )
                .toList();

        return new InMemoryRegisteredClientRepository(registeredClients);
    }

}
