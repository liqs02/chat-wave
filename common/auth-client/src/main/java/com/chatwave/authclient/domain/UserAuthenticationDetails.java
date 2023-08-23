package com.chatwave.authclient.domain;

import lombok.Getter;

@Getter
public class UserAuthenticationDetails {
    Long sessionId;
    String remoteAddress;
}
