package com.chatwave.chatservice.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("auth-service")
public interface AuthClient extends com.chatwave.authclient.client.AuthClient {}
