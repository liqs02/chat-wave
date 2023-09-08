package com.chatwave.authservice.domain.user;

import com.chatwave.authservice.domain.dto.request.AuthenticationRequest;
import com.chatwave.authservice.domain.dto.request.RegisterRequest;
import com.chatwave.authservice.domain.dto.response.AuthenticationResponse;
import com.chatwave.authservice.domain.dto.response.RegisterResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toUser(RegisterRequest registerRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toUser(AuthenticationRequest authenticationRequest);

    @Mapping(source = "id", target = "userId")
    RegisterResponse toCreateUserResponse(User user); // todo: add test

    @Mapping(source = "id", target = "userId")
    AuthenticationResponse toAuthenticateUserResponse(User user);
}
