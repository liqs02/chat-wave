package com.chatwave.accountservice.service;

import com.chatwave.accountservice.client.AuthService;
import com.chatwave.accountservice.domain.Account;
import com.chatwave.accountservice.domain.dto.AuthenticateUserRequest;
import com.chatwave.accountservice.domain.dto.CreateUserRequest;
import com.chatwave.accountservice.domain.dto.PatchPasswordRequest;
import com.chatwave.accountservice.domain.dto.TokenSet;
import com.chatwave.accountservice.repository.AccountRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    AuthService authService;

    private static TokenSet tokenSet;

    @BeforeAll
    public static void setup() {
        tokenSet = new TokenSet("refresh", "access");
    }

    @Test
    @DisplayName("createAccount() should create an account")
    public void t1() {
        var account = new Account();
        account.setLoginName("login");
        account.setDisplayName("display");

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
        var account = new Account();
        account.setId(1);

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
            var account = new Account();

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
    @DisplayName("getAccountByDisplayName()")
    class getAccountByDisplayName {
        @Test
        @DisplayName("should get an account")
        public void t1() {
            var account = new Account();

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
