package com.example.task_manager_backend.services;

import com.example.task_manager_backend.config.JwtProperties;
import com.example.task_manager_backend.models.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService{

    private static final String GENERATE_TOKEN_STARTED =
            "JWT access token generation started: userId={}, email={}";

    private static final String GENERATE_TOKEN_COMPLETED =
            "JWT access token generation completed: userId={}, expiresAt={}";

    private static final String EMAIL_CLAIM = "email";

    private final JwtProperties jwtProperties;

    @Override
    public String generateAccessToken(Long userId, String userEmail) {
        log.info(
                GENERATE_TOKEN_STARTED,
                userId,
                userEmail
        );

        Instant issuedAt = Instant.now();
        Instant expiresAt =
                issuedAt.plus(jwtProperties.accessTokenTTL());

        String accessToken = Jwts.builder()
                .subject(userId.toString())
                .claim(EMAIL_CLAIM, userEmail)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(getSigningKey())
                .compact();

        log.info(
                GENERATE_TOKEN_COMPLETED,
                userId,
                expiresAt
        );

        return accessToken;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes =
                Decoders.BASE64.decode(
                        jwtProperties.secret()
                );
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
