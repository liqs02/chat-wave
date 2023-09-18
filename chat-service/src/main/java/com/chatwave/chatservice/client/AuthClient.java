package com.chatwave.chatservice.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "auth-service", url = "${auth-service.url:}")
public interface AuthClient extends com.chatwave.authclient.client.AuthClient {}
