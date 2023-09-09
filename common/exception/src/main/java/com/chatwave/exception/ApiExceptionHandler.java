package com.chatwave.exception;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    protected ResponseEntity<ApiException> handleResponseStatusException(ResponseStatusException e) {
        var status = HttpStatus.valueOf(e.getStatusCode().value());

        var apiException = new ApiException(e.getReason(), status);
        return new ResponseEntity<>(apiException, status);
    }

    @ExceptionHandler(FeignException.class)
    protected ResponseEntity<ApiException> handleFeignException(FeignException e) { // todo : change it
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
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        var apiException = new ApiException(Objects.requireNonNull(e.getFieldError()).getDefaultMessage(), status);
        return new ResponseEntity<>(apiException, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        var apiException = new ApiException(ex.getMessage(), status);
        headers.setContentType(APPLICATION_JSON);
        return new ResponseEntity<>(apiException, headers, status);
    }

    @Override
    protected ResponseEntity<Object> createResponseEntity(@Nullable Object body, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        if(body instanceof ProblemDetail problemDetail) {
            var apiException = new ApiException(problemDetail.getDetail(), status);
            return new ResponseEntity<>(apiException, status);
        } else {
            var apiException = new ApiException("", status);
            return new ResponseEntity<>(apiException, status);
        }
    }
}
