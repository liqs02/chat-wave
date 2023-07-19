package com.chatwave.accountservice.exception;

import feign.FeignException;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class FeignExceptionHandler {
    @ExceptionHandler({ FeignException.class }) // TODO: test it
    public void handleFeignException(FeignException e) {
        var statusValue = e.status();
        throw new ResponseStatusException(HttpStatusCode.valueOf(statusValue) , e.getMessage());
    }
}
