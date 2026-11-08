package com.almotawaj.wallet.config.security;

import com.almotawaj.wallet.config.constants.LogMessages;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Verifies tokens issued by identity-service with the shared secret.
 * Wallet never issues tokens.
 */
@Slf4j
@Component
public class JwtUtils {
    private final SecretKey signingKey;

    public JwtUtils(@Value(SecurityConstants.JWT_SECRET_PROPERTY) String jwtSecret) {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public Optional<TokenClaims> parseToken(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
            String userId = claims.get(SecurityConstants.USER_ID_CLAIM, String.class);
            if (userId == null || claims.getSubject() == null) {
                return Optional.empty();
            }
            List<?> authorities = claims.get(SecurityConstants.AUTHORITIES_CLAIM, List.class);
            return Optional.of(new TokenClaims(UUID.fromString(userId), claims.getSubject(),
                    authorities == null ? List.of() : authorities.stream().map(Object::toString).toList()));
        } catch (JwtException | IllegalArgumentException e) {
            log.warn(LogMessages.INVALID_JWT, e.getMessage());
            return Optional.empty();
        }
    }
}
