package com.chatwave.authservice.domain;

import com.chatwave.authservice.domain.dto.TokenSetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SessionMapper {
    SessionMapper INSTANCE = Mappers.getMapper(SessionMapper.class);

    @Mapping(target = "refreshToken", source = "session.refreshToken")
    @Mapping(target = "accessToken", source = "session.accessToken")
    TokenSetResponse toTokenSetResponse(Session session);
}
