package com.example.task_manager_backend.services.jwt;

import com.example.task_manager_backend.security.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
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
public class JwtServiceImpl implements JwtService {

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
                issuedAt.plus(jwtProperties.accessTokenTtl());

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

    @Override
    public Long extractUserId(String accessToken) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(accessToken)
                .getPayload();

        return Long.parseLong(claims.getSubject());
    }

    @Override
    public boolean isAccessTokenValid(String accessToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(accessToken);

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT access token validation failed: reason={}", e.getMessage());
            return false;
        }
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes =
                Decoders.BASE64.decode(
                        jwtProperties.secret()
                );
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
