package com.chatwave.chatservice.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("auth-service")
public interface AuthService extends com.chatwave.authclient.client.AuthService {}
