package com.chatwave.authservice.domain.user;

import com.chatwave.authservice.domain.session.Session;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

@Getter
public class UserAuthenticationDetails {
    private final Long sessionId;
    private final String remoteAddress;

    public UserAuthenticationDetails(Session session, HttpServletRequest request) {
        this.sessionId = session.getId();
        this.remoteAddress = request.getRemoteAddr(); // TODO: most likely for further implementation.There may be a microservice address here instead of a client.
    }
}
