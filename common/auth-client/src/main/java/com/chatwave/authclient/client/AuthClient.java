package com.chatwave.authclient.client;

import com.chatwave.authclient.domain.UserAuthentication;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("auth-service")
@RequestMapping(consumes = "application/json", produces = "application/json")
public interface AuthClient {
    @GetMapping(value = "/users/authentication")
    UserAuthentication getUserAuthentication(@RequestHeader("User-Authorization") String userAuthorizationHeader);
}
