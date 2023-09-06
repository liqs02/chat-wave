package com.chatwave.authservice.domain.user;

import com.chatwave.authservice.domain.dto.AuthenticateUserRequest;
import com.chatwave.authservice.domain.dto.CreateUserRequest;
import com.chatwave.authservice.domain.dto.PatchUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "authorities", ignore = true)
    User toUser(CreateUserRequest createUserRequest);

    @Mapping(target = "authorities", ignore = true)
    User toUser(AuthenticateUserRequest authenticateUserRequest);

    @Mapping(source = "userId", target = "id")
    @Mapping(target = "authorities", ignore = true)
    User toUser(Integer userId, PatchUserRequest patchUserRequest);
}
