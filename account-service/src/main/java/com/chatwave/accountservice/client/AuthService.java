package com.chatwave.accountservice.client;

import com.chatwave.accountservice.domain.dto.AuthenticateUserRequest;
import com.chatwave.accountservice.domain.dto.CreateUserRequest;
import com.chatwave.accountservice.domain.dto.TokenSetResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("auth-service")
public interface AuthService {
    @PostMapping(value = "/users", consumes = "application/json")
    TokenSetResponse createUser(CreateUserRequest createUserRequest);

    @PostMapping(value = "/users/authenticate", consumes = "application/json")
    TokenSetResponse authenticateUser(AuthenticateUserRequest authenticateUserRequest);
}
