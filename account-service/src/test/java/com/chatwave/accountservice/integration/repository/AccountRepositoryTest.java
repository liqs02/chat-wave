package com.chatwave.accountservice.integration.repository;

import com.chatwave.accountservice.client.AuthClient;
import com.chatwave.accountservice.domain.Account;
import com.chatwave.accountservice.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DisplayName("AccountRepository")
public class AccountRepositoryTest {
    @MockBean
    AuthClient authService;
    @Autowired
    AccountRepository repository;
    Account account;

    @BeforeEach
    public void setup() {
        repository.deleteAll();

        account = new Account();
        account.setLoginName("login");
        account.setDisplayName("display");
        repository.save(account);
    }

    @Test
    @DisplayName("findByLoginName() should return account if exists with given loginName")
    public void t1() {
        var found = repository.findByLoginName("login").get();
        assertEquals(account.getId(), found.getId());
        assertEquals("login", found.getLoginName());
        assertEquals("display", found.getDisplayName());
    }

    @Test
    @DisplayName("findByLoginOrDisplayName() should return account if exists with given loginName")
    public void t2() {
        var found = repository.findByLoginOrDisplayName("login", null).get();
        assertEquals(account.getId(), found.getId());
        assertEquals("login", found.getLoginName());
        assertEquals("display", found.getDisplayName());
    }

    @Test
    @DisplayName("findByLoginOrDisplayName() should return account if exists with given displayName")
    public void t3() {
        var found = repository.findByLoginOrDisplayName(null, "display").get();
        assertEquals(account.getId(), found.getId());
        assertEquals("login", found.getLoginName());
        assertEquals("display", found.getDisplayName());
    }

}
