package com.chatwave.accountservice.unit.domain;

import com.chatwave.accountservice.domain.Account;
import com.chatwave.accountservice.domain.AccountMapper;
import com.chatwave.accountservice.domain.dto.CreateAccountRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("AccountMapper")
public class AccountMapperTest {
    private final AccountMapper mapper = AccountMapper.INSTANCE;
    private Account account;

    @BeforeEach
    void setup() {
        account = new Account();
        account.setId(1);
        account.setLoginName("login");
        account.setDisplayName("displayName");
    }

    @Test
    @DisplayName("should map CreateAccountRequest to Account entity")
    public void createAccountRequestToAccount() {
        var createUserRequest = new CreateAccountRequest("login", "displayName", "pass");
        var result = mapper.toAccount(createUserRequest);

        assertEquals(null, result.getId());
        assertEquals("login", result.getLoginName());
        assertEquals("displayName", result.getDisplayName());
    }

    @Test
    @DisplayName("should map Account entity to AccountShowcase")
    public void accountToAccountShowcase() {
        var showcase = mapper.toAccountShowcase(account);

        assertEquals(1, showcase.id());
        assertEquals("displayName", showcase.displayName());
    }

    @Test
    @DisplayName("should map Account entity to AccountDetails")
    public void accountToAccountDetails() {
        var showcase = mapper.toAccountDetails(account);

        assertEquals(1, showcase.id());
        assertEquals("login", showcase.loginName());
        assertEquals("displayName", showcase.displayName());
    }
}
