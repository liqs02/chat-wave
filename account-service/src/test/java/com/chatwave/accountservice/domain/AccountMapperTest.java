package com.chatwave.accountservice.domain;

import com.chatwave.accountservice.domain.dto.CreateAccountRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("AccountMapper")
public class AccountMapperTest {
    private final AccountMapper mapper = AccountMapper.INSTANCE;

    @Test
    @DisplayName("should map CreateAccountRequest to Account entity")
    public void createAccountRequestToAccount() {
        var createUserRequest = new CreateAccountRequest("login", "nick", "pass");
        var account = mapper.toAccount(createUserRequest);

        assertEquals(null, account.getId());
        assertEquals("login", account.getLoginName());
        assertEquals("nick", account.getDisplayName());
    }
}
