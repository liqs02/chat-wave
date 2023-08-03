package com.chatwave.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.SECONDS;

@Getter
public class ApiException {
    private String message;
    private HttpStatusCode status;
    private LocalDateTime timestamp;

    public ApiException(String message, HttpStatusCode status) {
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now().truncatedTo(SECONDS);
    }
}
