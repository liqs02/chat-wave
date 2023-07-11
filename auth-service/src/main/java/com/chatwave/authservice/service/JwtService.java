package com.chatwave.authservice.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;

public interface JwtService {
    /**
     * Creates a JWT for a given user.
     *
     * @param user
     * @return generated json web token
     * */
    String generateToken(UserDetails user);

    /**
     * Creates a JWT for a given user with attached extra claims.
     *
     * @param user
     * @param extraClaims
     * @return generated json web token
     * */
    String generateToken(UserDetails user, HashMap<String, Object> extraClaims);

    /**
     * Checks that the JWT is not expired and matches with given user.
     *
     * @param token
     * @param user
     * @return a boolean representing whether the JWT is valid
     * */
    boolean isTokenValid(String token, UserDetails user);

    /**
     * Extracts username from JWT.
     *
     * @param token
     * @return username
     */
    String extractUsername(String token);

    /**
     * Gets all claims from JWT.
     * @param token
     * @return all token's claims
     */
    Claims extractAllClaims(String token);
}
