package com.chatwave.accountservice.unit.service;

import com.chatwave.accountservice.client.AuthClient;
import com.chatwave.accountservice.client.dto.RegisterResponse;
import com.chatwave.accountservice.domain.Account;
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

import static com.chatwave.accountservice.utils.TestVariables.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService")
public class AccountServiceTest {
    @InjectMocks
    private AccountServiceImpl service;
    @Mock
    private AccountRepository repository;
    @Mock
    private AuthClient authService;
    private Account account;

    @BeforeEach
    void setup() {
        account = new Account();
        account.setDisplayName(DISPLAY_NAME);
    }

    @Test
    @DisplayName("createAccount() should create an account")
    public void t1() {
        when(
            authService.createUser(REGISTER_REQUEST)
        ).thenReturn(new RegisterResponse(USER_ID));

        when(
                authService.createSessions(CREATE_SESSION_REQUEST)
        ).thenReturn(TOKEN_SET);

        var result = service.createAccount(account, LOGIN_NAME, PASSWORD);
        assertEquals(TOKEN_SET, result);

        verify(
                repository, times(1)
        ).save(account);
    }

    @Test
    @DisplayName("authenticateAccount() should authenticate an account")
    public void t2() {
        when(
                authService.authenticateUser(AUTHENTICATION_REQUEST)
        ).thenReturn(AUTHENTICATION_RESPONSE);

        when(
                authService.createSessions(CREATE_SESSION_REQUEST)
        ).thenReturn(TOKEN_SET);

        var result = service.authenticateAccount(AUTHENTICATION_REQUEST);

        assertEquals(TOKEN_SET, result);
    }

    @Nested
    @DisplayName("getAccountById()")
    class getAccountById {
        @Test
        @DisplayName("should get an account")
        public void t1() {
            when(
                    repository.findById(USER_ID)
            ).thenReturn(Optional.of(account));

            var result = service.getAccountById(USER_ID);

            assertEquals(account, result);
        }
        @Test
        @DisplayName("should throw NOT_FOUND ResponseStatusException if account does not exist")
        public void t2() {
            var result = assertThrows(
                    ResponseStatusException.class,
                    () -> service.getAccountById(USER_ID)
            );

            assertEquals(NOT_FOUND, result.getStatusCode());
        }
    }

    @Test
    @DisplayName("patchAccount() should update user's displayName and password")
    public void t3() {
        var account = new Account();
        account.setId(USER_ID);

        when(
                repository.findById(USER_ID)
        ).thenReturn(Optional.of(account));

        service.patchAccount(USER_ID, PATCH_ACCOUNT_REQUEST);

        verify(
                repository, times(1)
        ).save(account);

        verify(
                authService, times(1)
        ).patchUser(USER_ID, PATCH_USER_REQUEST);
    }
}
