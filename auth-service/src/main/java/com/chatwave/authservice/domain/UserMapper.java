package com.chatwave.authservice.domain;

import com.chatwave.authservice.domain.dto.CreateUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "authorities", ignore = true)
    User toUser(CreateUserRequest createUserRequest);
}
