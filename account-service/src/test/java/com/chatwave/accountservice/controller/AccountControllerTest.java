package com.chatwave.accountservice.controller;

import com.chatwave.accountservice.domain.Account;
import com.chatwave.accountservice.domain.AccountMapper;
import com.chatwave.accountservice.domain.dto.*;
import com.chatwave.accountservice.service.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountControllerTest")
public class AccountControllerTest {
        @InjectMocks
        private AccountController controller;
        @Mock
        private AccountService service;
        @Mock
        private AccountMapper mapper;

        private final TokenSet tokenSet = new TokenSet("refresh", "access");

        @Nested
        @DisplayName("createAccount()")
        class CreateAccount {
                @Test
                @DisplayName("should create an account")
                public void t1() {
                        var account = new Account();
                        account.setLoginName("login");
                        account.setDisplayName("display");

                        var createAccountRequest = new CreateAccountRequest("login", "display", "pass");

                        when(
                                mapper.toAccount(createAccountRequest)
                        ).thenReturn(account);

                        when(
                                service.createAccount(account, "pass")
                        ).thenReturn(tokenSet);

                        var result = controller.createAccount(createAccountRequest);
                        assertEquals(tokenSet, result);
                }
        }

        @Nested
        @DisplayName("authenticateAccount()")
        class AuthenticateAccount {
                @Test
                @DisplayName("should authenticate an account")
                public void t1() {
                        var authenticateAccountRequest = new AuthenticateAccountRequest("login", "pass");

                        when(
                                service.authenticateAccount("login", "pass")
                        ).thenReturn(tokenSet);

                        var result = controller.authenticateAccount(authenticateAccountRequest);
                        assertEquals(tokenSet, result);
                }
        }

        @Nested
        @DisplayName("getAccountDetails()")
        class GetAccountDetails {
                @Test
                @DisplayName("should return an AccountDetails")
                public void t1() {
                        var account = new Account();
                        var accountDetails = new AccountDetails(1, "login", "display");

                        when(
                                service.getAccount(1)
                        ).thenReturn(account);

                        when(
                                mapper.toAccountDetails(account)
                        ).thenReturn(accountDetails);

                        var result = controller.getAccountDetails(1);
                        assertEquals(accountDetails, result);
                }
        }

        @Nested
        @DisplayName("getAccountShowcase()")
        class GetAccountShowcase {
                @Test
                @DisplayName("should return an AccountShowcase")
                public void t1() {
                        var account = new Account();
                        var accountShowcase = new AccountShowcase(1, "display");

                        when(
                                service.getAccount(1)
                        ).thenReturn(account);

                        when(
                                mapper.toAccountShowcase(account)
                        ).thenReturn(accountShowcase);

                        var result = controller.getAccountShowcase(1);
                        assertEquals(accountShowcase, result);
                }
        }
}
