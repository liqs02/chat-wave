package com.chatwave.authservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Client {
    private String id;
    private String secret;
    private String url;

    public String getUrl() {
        return this.url + "/login/oauth2/code/spring";
    }
}
