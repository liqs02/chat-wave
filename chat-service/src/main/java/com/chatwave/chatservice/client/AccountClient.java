package com.chatwave.chatservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@FeignClient(name = "account-service", url = "${account-service.url:}")
public interface AccountClient {
    @GetMapping(value = "/accounts/{accountId}/exist", consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    void doesAccountExist(@PathVariable Integer accountId);
}
