package com.chatwave.authservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {
    @Value("${JWT_SECRET_KEY:0}") // TODO: validate it when is set
    private String secretKey;

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateToken(UserDetails user) {
        return generateToken(user, new HashMap<>());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateToken(UserDetails user, HashMap<String, Object> extraClaims) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject( user.getUsername() )
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 120_000 /*2 minutes*/))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTokenValid(String token, UserDetails user) {
        var username = extractUsername(token);
        return username.equals(user.getUsername()) && !isTokenExpired(token);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    private Key getSignInKey() {
        var keyBytes = Decoders.BASE64.decode(secretKey);
        if(keyBytes.length * 8 != 256) {
            log.warn("JWT secret key contains incorrect value. The secret key should be 256 bits long. Current number of bits: " + keyBytes.length * 8);
            throw new ResponseStatusException(SERVICE_UNAVAILABLE, "Service cannot handle the request temporarily. Try again in a moment.");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
