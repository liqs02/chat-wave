package com.chatwave.accountservice.domain;

import lombok.Getter;

@Getter
public class UserAuthenticationDetails {
    Long sessionId;
    String remoteAddress;
}
