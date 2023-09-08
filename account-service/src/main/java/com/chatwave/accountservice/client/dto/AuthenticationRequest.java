package com.chatwave.accountservice.client.dto;

import jakarta.validation.constraints.NotEmpty;

public record AuthenticationRequest(String loginName, String password){}
