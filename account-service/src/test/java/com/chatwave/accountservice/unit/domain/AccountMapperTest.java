package com.chatwave.accountservice.unit.domain;

import com.chatwave.accountservice.domain.Account;
import com.chatwave.accountservice.domain.AccountMapper;
import com.chatwave.accountservice.domain.dto.CreateAccountRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.chatwave.accountservice.utils.TestVariables.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("AccountMapper")
public class AccountMapperTest {
    private final AccountMapper mapper = AccountMapper.INSTANCE;
    private Account account;

    @BeforeEach
    void setup() {
        account = new Account();
        account.setId(USER_ID);
        account.setDisplayName(DISPLAY_NAME);
    }

    @Test
    @DisplayName("should map CreateAccountRequest to Account entity")
    public void createAccountRequestToAccount() {
        var createUserRequest = new CreateAccountRequest(LOGIN_NAME, DISPLAY_NAME, PASSWORD);
        var result = mapper.toAccount(createUserRequest);

        assertEquals(null, result.getId());
        assertEquals(DISPLAY_NAME, result.getDisplayName());
    }

    @Test
    @DisplayName("should map Account entity to AccountResponse")
    public void accountToAccountShowcase() {
        var showcase = mapper.toAccountShowcase(account);

        assertEquals(USER_ID, showcase.id());
        assertEquals(DISPLAY_NAME, showcase.displayName());
    }
}
