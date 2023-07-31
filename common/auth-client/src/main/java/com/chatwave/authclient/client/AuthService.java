package com.chatwave.authclient.client;

import com.chatwave.authclient.domain.UserAuthentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

public interface AuthService {
    @GetMapping(value = "/users/authentication")
    UserAuthentication getUserAuthentication(@RequestHeader("User-Authorization") String userAuthorizationHeader);
}
