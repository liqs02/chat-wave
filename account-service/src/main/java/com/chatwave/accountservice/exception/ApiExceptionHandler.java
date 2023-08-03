package com.chatwave.accountservice.exception;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler extends ResponseEntityExceptionHandler { // TODO: move it to separate library and add to chat-service
    @ExceptionHandler(ResponseStatusException.class)
    protected ResponseEntity<ApiException> handleResponseStatusException(ResponseStatusException e) {
        var status = HttpStatus.valueOf(e.getStatusCode().value());

        var apiException = new ApiException(e.getReason(), status);
        return new ResponseEntity<>(apiException, status);
    }


    @ExceptionHandler(FeignException.class)
    protected ResponseEntity<ApiException> handleFeignException(FeignException e) {
        var status = HttpStatus.valueOf(e.status());
        if(status.is5xxServerError()) {
            log.info("Feign client throw " + status + " error: " + e.getMessage());
            var apiException = new ApiException("An unexpected error occurred.", INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(apiException, status);
        }

        var exceptionMessage = e.getMessage();
        var startIndex = exceptionMessage.indexOf("\"message\":\"");
        if(startIndex == -1)
            return new ResponseEntity<>(new ApiException(exceptionMessage, status), status);
        startIndex += "\"message\":\"".length();

        var endIndex = exceptionMessage.indexOf("\"", startIndex);
        if(endIndex == -1)
            return new ResponseEntity<>(new ApiException(exceptionMessage, status), status);

        var message = exceptionMessage.substring(startIndex, endIndex);

        var apiException = new ApiException(message, status);
        return new ResponseEntity<>(apiException, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        var apiException = new ApiException(Objects.requireNonNull(e.getFieldError()).getDefaultMessage(), status);
        return new ResponseEntity<>(apiException, status);
    }
}
