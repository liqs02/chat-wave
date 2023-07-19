package com.chatwave.accountservice.client;

import com.chatwave.accountservice.domain.dto.AuthenticateUserRequest;
import com.chatwave.accountservice.domain.dto.CreateUserRequest;
import com.chatwave.accountservice.domain.dto.TokenSetResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "auth-service")
public interface AuthService {
    @PostMapping("/users")
    TokenSetResponse createUser(CreateUserRequest createUserRequest);

    @PostMapping("/users/authenticate")
    TokenSetResponse authenticateUser(AuthenticateUserRequest authenticateUserRequest);
}
