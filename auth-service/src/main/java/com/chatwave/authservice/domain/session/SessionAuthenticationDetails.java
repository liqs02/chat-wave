package com.chatwave.authservice.domain.session;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

@Getter
public class SessionAuthenticationDetails {
    Long sessionId;
    String remoteAddress;

    public SessionAuthenticationDetails(Session session, HttpServletRequest request) {
        this.sessionId = session.getId();
        this.remoteAddress = request.getRemoteAddr(); // TODO: most likely for further implementation.There may be a microservice address here instead of a client.
    }
}
