package com.chatwave.chatservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.fail;

public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(Object object) {
        try{
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            fail("Object could not be mapped to json string.");
            throw new RuntimeException(e);
        }
    }
}
