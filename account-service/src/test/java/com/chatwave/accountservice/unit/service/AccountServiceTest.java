package com.chatwave.accountservice.unit.service;

import com.chatwave.accountservice.client.AuthClient;
import com.chatwave.accountservice.domain.Account;
import com.chatwave.accountservice.domain.dto.AuthenticateUserRequest;
import com.chatwave.accountservice.domain.dto.CreateUserRequest;
import com.chatwave.accountservice.domain.dto.PatchPasswordRequest;
import com.chatwave.accountservice.domain.dto.TokenSet;
import com.chatwave.accountservice.repository.AccountRepository;
import com.chatwave.accountservice.service.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService")
public class AccountServiceTest {
    @InjectMocks
    AccountServiceImpl service;
    @Mock
    AccountRepository repository;
    @Mock
    AuthClient authService;
    private TokenSet tokenSet;
    private Account account;

    @BeforeEach
    void setup() {
        tokenSet = new TokenSet("refresh", "access");
        account = new Account();
        account.setId(1);
        account.setLoginName("login");
        account.setDisplayName("displayName");
    }

    @Test
    @DisplayName("createAccount() should create an account")
    public void t1() {
        var accountWithId = account;
        accountWithId.setId(1);

        when(
                repository.save(account)
        ).thenReturn(accountWithId);

        when(
            authService.createUser(new CreateUserRequest(1, "pass"))
        ).thenReturn(tokenSet);

        var result = service.createAccount(account, "pass");
        assertEquals(tokenSet, result);
    }

    @Test
    @DisplayName("authenticateAccount() should authenticate an account")
    public void t2() {
        when(
                repository.findByLoginName("login")
        ).thenReturn(Optional.of(account));

        when(
                authService.authenticateUser(new AuthenticateUserRequest(1, "pass"))
        ).thenReturn(tokenSet);

        var result = service.authenticateAccount("login", "pass");

        assertEquals(tokenSet, result);
    }

    @Nested
    @DisplayName("getAccountById()")
    class getAccountById {
        @Test
        @DisplayName("should get an account")
        public void t1() {
            when(
                    repository.findById(1)
            ).thenReturn(Optional.of(account));

            var result = service.getAccountById(1);

            assertEquals(account, result);
        }
        @Test
        @DisplayName("should throw NOT_FOUND ResponseStatusException if account does not exist")
        public void t2() {
            var thrown = assertThrows(
                    ResponseStatusException.class,
                    () -> service.getAccountById(1)
            );

            assertEquals(NOT_FOUND, thrown.getStatusCode());
        }
    }

    @Nested
    @DisplayName("doesAccountExist()")
    class doesAccountExist {
        @Test
        @DisplayName("should get an account and return true")
        public void t1() {
            when(
                    repository.findById(1)
            ).thenReturn(Optional.of(account));

            var result = service.doesAccountExist(1);

            assertTrue(result);
        }

        @Test
        @DisplayName("should get an account and return false if account doesn't exists")
        public void t2() {
            when(
                    repository.findById(1)
            ).thenReturn(Optional.empty());

            var result = service.doesAccountExist(1);

            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("getAccountByDisplayName()")
    class getAccountByDisplayName {
        @Test
        @DisplayName("should get an account")
        public void t1() {
            when(
                    repository.findByDisplayName("display")
            ).thenReturn(Optional.of(account));

            var result = service.getAccountByDisplayName("display");

            assertEquals(account, result);
        }

        @Test
        @DisplayName("should throw NOT_FOUND ResponseStatusException if account does not exist")
        public void t2() {
            var thrown = assertThrows(
                    ResponseStatusException.class,
                    () -> service.getAccountById(1)
            );

            assertEquals(NOT_FOUND, thrown.getStatusCode());
        }
    }

    @Test
    @DisplayName("patchAccountPassword() should change password in auth service")
    public void t3() {
        var patchPasswordRequest = new PatchPasswordRequest("pass", "new");

        service.patchAccountPassword(1, patchPasswordRequest);

        verify(
                authService, times(1)
        ).patchUserPassword(1, patchPasswordRequest);
    }
}
