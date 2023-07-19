package com.chatwave.accountservice.repository;

import com.chatwave.accountservice.domain.Account;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DisplayName("AccountRepository")
public class AccountRepositoryTest {
    @Autowired
    AccountRepository repository;

    Account account;

    @BeforeEach
    public void setup() {
        repository.deleteAll();

        account = new Account();
        account.setLoginName("login");
        account.setDisplayName("nick");
        repository.save(account);
    }


    @Test
    @DisplayName("findByLoginName() should return account if exists")
    public void t1() {
        var found = repository.findByLoginName("login").get();
        assertEquals(account.getId(), found.getId());
        assertEquals("login", found.getLoginName());
        assertEquals("nick", found.getDisplayName());
    }
}
