package com.chatwave.accountservice.domain;

import com.chatwave.accountservice.domain.dto.CreateAccountRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountMapper {
    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "followed", ignore = true)
    Account toAccount(CreateAccountRequest createAccountRequest);
}
