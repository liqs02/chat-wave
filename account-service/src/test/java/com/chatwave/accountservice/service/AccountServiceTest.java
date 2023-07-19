package com.chatwave.accountservice.service;

import com.chatwave.accountservice.client.AuthService;
import com.chatwave.accountservice.domain.Account;
import com.chatwave.accountservice.domain.dto.CreateUserRequest;
import com.chatwave.accountservice.domain.dto.TokenSetResponse;
import com.chatwave.accountservice.repository.AccountRepository;
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
@DisplayName("AccountService")
public class AccountServiceTest {
    @InjectMocks
    AccountServiceImpl service;
    @Mock
    AccountRepository repository;
    @Mock
    AuthService authService;

    @Nested
    @DisplayName("createAccount()")
    class CreateAccount {
        @Test
        @DisplayName("should create an account")
        public void t1() {
            var account = new Account();
            account.setLoginName("login");
            account.setDisplayName("display");

            var accountWithId = account;
            accountWithId.setId(1);

            var tokenSet = new TokenSetResponse("refresh", "access");

            when(
                    repository.save(account)
            ).thenReturn(accountWithId);

            when(
                authService.createUser(new CreateUserRequest(1, "pass"))
            ).thenReturn(tokenSet);

            var result = service.createAccount(account, "pass");
            assertEquals(tokenSet, result);
        }
    }
}
