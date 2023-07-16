package com.chatwave.authservice.domain.session;

import com.chatwave.authservice.domain.dto.SessionResponse;
import com.chatwave.authservice.domain.dto.TokenSetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SessionMapper {
    SessionMapper INSTANCE = Mappers.getMapper(SessionMapper.class);

    TokenSetResponse toTokenSetResponse(Session session);

    SessionResponse toSessionResponse(Session session);
}
