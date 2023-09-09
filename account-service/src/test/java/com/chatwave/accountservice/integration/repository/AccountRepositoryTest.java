package com.chatwave.accountservice.integration.repository;

import com.chatwave.accountservice.domain.Account;
import com.chatwave.accountservice.repository.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.chatwave.accountservice.utils.TestVariables.DISPLAY_NAME;
import static com.chatwave.accountservice.utils.TestVariables.USER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@DisplayName("AccountRepository")
public class AccountRepositoryTest {
    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    public void setup() {
        var account = new Account();
        account.setId(USER_ID);
        account.setDisplayName(DISPLAY_NAME);
        accountRepository.save(account);
    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("findByDisplayName() should return account if exists with given displayName")
    public void t3() {
        var found = accountRepository.findByDisplayName(DISPLAY_NAME);
        if(found.isEmpty())
            fail();

        var account = found.get();

        assertEquals(USER_ID, account.getId());
        assertEquals(DISPLAY_NAME, account.getDisplayName());
    }

}
