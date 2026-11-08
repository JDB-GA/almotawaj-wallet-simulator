package com.almotawaj.wallet.audit.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Trusts the token issued by identity-service: checks its signature with the shared secret
 * and reads the authorities from it, without calling identity-service.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final SecretKey signingKey;

    public JwtAuthenticationFilter(@Value(AuditConstants.JWT_SECRET_PROPERTY) String jwtSecret) {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = extractToken(request);
        if (token != null) {
            try {
                Claims claims = Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
                List<?> authorities = claims.get(AuditConstants.AUTHORITIES_CLAIM, List.class);
                var granted = authorities == null ? List.<SimpleGrantedAuthority>of()
                        : authorities.stream().map(a -> new SimpleGrantedAuthority(a.toString())).toList();
                SecurityContextHolder.getContext().setAuthentication(
                        UsernamePasswordAuthenticationToken.authenticated(claims.getSubject(), null, granted));
            } catch (JwtException | IllegalArgumentException ignored) {
                // invalid token: the request stays anonymous and is refused by the security rules
            }
        }
        chain.doFilter(request, response);
    }

    private static String extractToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(AuditConstants.BEARER_PREFIX)) {
            return header.substring(AuditConstants.BEARER_PREFIX.length());
        }
        Cookie cookie = WebUtils.getCookie(request, AuditConstants.AUTH_COOKIE_NAME);
        return cookie != null && !cookie.getValue().isBlank() ? cookie.getValue() : null;
    }
}
