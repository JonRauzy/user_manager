package com.jon.user_manager.util.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {
    private final SecretKey secretKey;
    private final long expiration;

    JwtUtils(
            @Value("${app.jwt.secret}")String secret,
            @Value("${app.jwt.expiration}") long expiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
    }

    public String generateToken(String subject, Map<String, Object> claims) {
        Date now = new Date();

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(now.getTime() + expiration))
                .signWith(secretKey)
                .compact();
    }

    public boolean isValid(String token) {
        Date tokenExpiration = parse(token).getPayload().getExpiration();
        return tokenExpiration.after(new Date());
    }

    public String getSubject(String token) {
        Claims body = parse(token).getPayload();
        //TODO check it in the debugger
        return body.getSubject();
    }

}
