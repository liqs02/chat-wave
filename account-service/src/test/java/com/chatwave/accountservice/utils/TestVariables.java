package com.chatwave.accountservice.utils;

import com.chatwave.accountservice.client.dto.*;
import com.chatwave.accountservice.domain.dto.PatchAccountRequest;

public class TestVariables {
    public static final Integer USER_ID = 1;
    public static final String LOGIN_NAME = "loginName";
    public static final String DISPLAY_NAME = "displayName";
    public static final String DISPLAY_NAME_2 = "NewDisplayName";
    public static final String PASSWORD = "Pass1234";
    public static final String PASSWORD_2 = "1234Pass";

    public static final String ACCESS_TOKEN = "accessTokenValue";
    public static final String REFRESH_TOKEN = "refreshTokenValue";

    public static final TokenSet TOKEN_SET = new TokenSet(REFRESH_TOKEN, ACCESS_TOKEN);
    public static final String BEARER_TOKEN = "Bearer accessToken";

    public static final RegisterRequest REGISTER_REQUEST = new RegisterRequest(LOGIN_NAME, PASSWORD);
    public static final AuthenticationRequest AUTHENTICATION_REQUEST = new AuthenticationRequest(LOGIN_NAME, PASSWORD);
    public static final AuthenticationResponse AUTHENTICATION_RESPONSE = new AuthenticationResponse(USER_ID);
    public static final CreateSessionRequest CREATE_SESSION_REQUEST = new CreateSessionRequest(USER_ID);
    public static final PatchUserRequest PATCH_USER_REQUEST = new PatchUserRequest(PASSWORD_2);
    public static final PatchAccountRequest PATCH_ACCOUNT_REQUEST = new PatchAccountRequest(DISPLAY_NAME_2, PASSWORD_2);

}
