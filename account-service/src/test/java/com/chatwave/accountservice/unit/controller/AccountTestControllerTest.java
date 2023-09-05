package com.chatwave.accountservice.unit.controller;

import com.chatwave.accountservice.controller.AccountController;
import com.chatwave.accountservice.domain.Account;
import com.chatwave.accountservice.domain.AccountMapper;
import com.chatwave.accountservice.domain.dto.*;
import com.chatwave.accountservice.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountTestControllerTest")
public class AccountTestControllerTest {
        @InjectMocks
        private AccountController controller;
        @Mock
        private AccountService service;
        @Mock
        private AccountMapper mapper;
        private final TokenSet TOKEN_SET = new TokenSet("refresh", "access");
        private Account account;

        @BeforeEach
        void setup() {
                account = new Account();
                account.setId(1);
                account.setLoginName("login");
                account.setDisplayName("displayName");
        }

        @Test
        @DisplayName("createAccount() should create an account")
        public void createAccount() {
                account.setId(null);

                var createAccountRequest = new CreateAccountRequest("login", "display", "pass");

                when(
                        mapper.toAccount(createAccountRequest)
                ).thenReturn(account);

                when(
                        service.createAccount(account, "pass")
                ).thenReturn(TOKEN_SET);

                var result = controller.createAccount(createAccountRequest);
                assertEquals(TOKEN_SET, result);
        }

        @Test
        @DisplayName("authenticateAccount() should authenticate an account")
        public void authenticateAccount() {
                var authenticateAccountRequest = new AuthenticateAccountRequest("login", "pass");

                when(
                        service.authenticateAccount("login", "pass")
                ).thenReturn(TOKEN_SET);

                var result = controller.authenticateAccount(authenticateAccountRequest);
                assertEquals(TOKEN_SET, result);
        }

        @Nested
        @DisplayName("doesAccountExist()")
        class doesAccountExist {
                @Test
                @DisplayName("should return nothing if user exist")
                public void t1() {
                        when(
                                service.doesAccountExist(1)
                        ).thenReturn(true);

                        controller.doesAccountExist(1);
                }

                @Test
                @DisplayName("should throw NOT_FOUND status if user doesn't exist")
                public void t2() {
                        when(
                                service.doesAccountExist(1)
                        ).thenReturn(false);

                        var result = assertThrows(
                                ResponseStatusException.class,
                                () -> controller.doesAccountExist(1)
                        );

                        assertEquals(NOT_FOUND, result.getStatusCode());
                }
        }


        @Test
        @DisplayName("getAccountShowcase() should return an AccountShowcase")
        public void getAccountShowcase() {
                var accountShowcase = new AccountShowcase(1, "display");

                when(
                        service.getAccountById(1)
                ).thenReturn(account);

                when(
                        mapper.toAccountShowcase(account)
                ).thenReturn(accountShowcase);

                var result = controller.getAccountShowcase(1);
                assertEquals(accountShowcase, result);
        }

        @Test
        @DisplayName("patchAccount() should invoke service's change password method")
        public void patchAccountPassword() {
                var patchPasswordRequest = new PatchAccountRequest("pass", "new");

                controller.patchAccount(1, patchPasswordRequest);

                verify(
                        service
                ).patchAccount(1, patchPasswordRequest);
        }
}
