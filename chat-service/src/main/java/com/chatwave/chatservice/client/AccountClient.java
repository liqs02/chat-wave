package com.chatwave.chatservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@FeignClient("account-service")
@RequestMapping(consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
public interface AccountClient {
    @GetMapping("/{accountId}/exist")
    void doesAccountExist(@PathVariable Integer accountId);
}
