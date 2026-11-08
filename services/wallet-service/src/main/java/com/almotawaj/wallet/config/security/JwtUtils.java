package com.almotawaj.wallet.config.security;

import com.almotawaj.wallet.config.constants.LogMessages;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
public class JwtUtils {
    private final SecretKey signingKey;
    private final long jwtExpirationMs;

    public JwtUtils(@Value(SecurityConstants.JWT_SECRET_PROPERTY) String jwtSecret,
                    @Value(SecurityConstants.JWT_EXPIRATION_PROPERTY) long jwtExpirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationMs = jwtExpirationMs;
    }

    public String generateToken(UserDetails userDetails) {
        Date now = new Date();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + jwtExpirationMs))
                .signWith(signingKey)
                .compact();
    }

    public Optional<TokenClaims> parseToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Optional.ofNullable(claims.getSubject())
                    .map(subject -> new TokenClaims(subject, claims.getIssuedAt().toInstant()));
        } catch (JwtException | IllegalArgumentException e) {
            log.warn(LogMessages.INVALID_JWT, e.getMessage());
            return Optional.empty();
        }
    }
}
