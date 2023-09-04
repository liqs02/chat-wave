package com.chatwave.authclient.domain;

import lombok.Data;

@Data
public class UserAuthenticationDetails {
    private Long sessionId;
    private String remoteAddress;
}
