package com.chatwave.accountservice.controller;

import com.chatwave.accountservice.domain.Account;
import com.chatwave.accountservice.domain.AccountMapper;
import com.chatwave.accountservice.domain.dto.*;
import com.chatwave.accountservice.service.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
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


        @Test
        @DisplayName("createAccount() should create an account")
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

        @Test
        @DisplayName("authenticateAccount() should authenticate an account")
        public void t2() {
                var authenticateAccountRequest = new AuthenticateAccountRequest("login", "pass");

                when(
                        service.authenticateAccount("login", "pass")
                ).thenReturn(tokenSet);

                var result = controller.authenticateAccount(authenticateAccountRequest);
                assertEquals(tokenSet, result);
        }

        @Test
        @DisplayName("getAccountDetails() should return an AccountDetails")
        public void t3() {
                var account = new Account();
                var accountDetails = new AccountDetails(1, "login", "display");

                when(
                        service.getAccountById(1)
                ).thenReturn(account);

                when(
                        mapper.toAccountDetails(account)
                ).thenReturn(accountDetails);

                var result = controller.getAccountDetails(1);
                assertEquals(accountDetails, result);
        }


        @Test
        @DisplayName("getAccountShowcase() should return an AccountShowcase")
        public void t4() {
                var account = new Account();
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
        @DisplayName("getAccountByDisplayName() should return an AccountShowcase")
        public void t5() {
                var account = new Account();
                var accountShowcase = new AccountShowcase(1, "display");

                when(
                        service.getAccountByDisplayName("display")
                ).thenReturn(account);

                when(
                        mapper.toAccountShowcase(account)
                ).thenReturn(accountShowcase);

                var result = controller.getAccountByDisplayName("display");
                assertEquals(accountShowcase, result);
        }

        @Test
        @DisplayName("patchAccountPassword() should invoke service's change password method")
        public void t6() {
                var patchPasswordRequest = new PatchPasswordRequest("pass", "new");

                controller.patchAccountPassword(1, patchPasswordRequest);

                verify(
                        service
                ).patchAccountPassword(1, patchPasswordRequest);
        }
}
