package com.almotawaj.wallet.config.security;

import com.almotawaj.wallet.config.constants.SecurityConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class AuthCookieFactory {
    private final boolean secure;
    private final Duration maxAge;

    public AuthCookieFactory(@Value(SecurityConstants.AUTH_COOKIE_SECURE_PROPERTY) boolean secure,
                             @Value(SecurityConstants.JWT_EXPIRATION_PROPERTY) long jwtExpirationMs) {
        this.secure = secure;
        this.maxAge = Duration.ofMillis(jwtExpirationMs);
    }

    public ResponseCookie create(String token) {
        return baseCookie(token).maxAge(maxAge).build();
    }

    public ResponseCookie clear() {
        return baseCookie("").maxAge(Duration.ZERO).build();
    }

    private ResponseCookie.ResponseCookieBuilder baseCookie(String value) {
        return ResponseCookie.from(SecurityConstants.AUTH_COOKIE_NAME, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite(SecurityConstants.AUTH_COOKIE_SAME_SITE)
                .path(SecurityConstants.AUTH_COOKIE_PATH);
    }
}
