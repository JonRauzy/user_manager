package com.jon.user_manager.util.security;

import com.jon.user_manager.auth.RefreshToken;
import com.jon.user_manager.auth.RefreshTokenRepository;
import com.jon.user_manager.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtUtils {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.secret}")
    String secretKey;

    @Value("${app.jwt.expiration}")
    long accessExpiration;

    @Value("${app.jwt.refresh-expiration}")
    long refreshExpiration;


    private Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
    }

    public String generateAccessToken(User user) {
        return generateToken(user, accessExpiration);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, refreshExpiration);
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

    private SecretKey getSigningKey() {
        byte[] keyBites = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBites);
    }

    private String generateToken(User user, long expiration) {
        Map<String, Object> claims = getClaims(user);
        String subject = user.getEmail();

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    private Map<String, Object> getClaims(User user){
        return Map.of(
                "userId", user.getId(),
                "role", "USER" //TODO create + use ROLE
        );
    }

}
