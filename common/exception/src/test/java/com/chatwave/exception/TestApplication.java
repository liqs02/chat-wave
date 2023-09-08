package com.chatwave.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
public class TestApplication {
    public static void main(String[] args) {
            SpringApplication.run(TestApplication.class, args);
    }

    @RestController
    @RequiredArgsConstructor
    @RequestMapping(consumes = "application/json", produces = "application/json")
    @Profile("test")
    public static class TestController {
        @GetMapping
        public String test() {
            return "Hello world!";
        }
    }
}
