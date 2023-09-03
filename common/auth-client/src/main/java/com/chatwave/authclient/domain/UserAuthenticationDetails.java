package com.chatwave.authclient.domain;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
public class UserAuthenticationDetails {
    private Long sessionId;
    private String remoteAddress;

    public void setRemoteAddress(HttpServletRequest request) {
        this.remoteAddress = request.getRemoteAddr();
    }
}
