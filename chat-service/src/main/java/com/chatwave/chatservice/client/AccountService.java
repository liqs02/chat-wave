package com.chatwave.chatservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("account-service")
public interface AccountService {
    @GetMapping("/{accountId}/exist")
    void doesAccountExist(@PathVariable Integer accountId);
}
