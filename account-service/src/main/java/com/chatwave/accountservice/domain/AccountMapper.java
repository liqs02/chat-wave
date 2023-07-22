package com.chatwave.accountservice.domain;

import com.chatwave.accountservice.domain.dto.AccountDetails;
import com.chatwave.accountservice.domain.dto.AccountShowcase;
import com.chatwave.accountservice.domain.dto.CreateAccountRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountMapper {
    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);
    @Mapping(target = "id", ignore = true)
    Account toAccount(CreateAccountRequest createAccountRequest);

    AccountShowcase toAccountShowcase(Account account);

    AccountDetails toAccountDetails(Account account);
}
