package com.almotawaj.wallet.config.security;

import com.almotawaj.wallet.config.constants.LogMessages;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private final MyUserDetailsService myUserDetailsService;
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            jwtUtils.parseToken(token).ifPresent(claims -> authenticate(claims, request));
        }

        filterChain.doFilter(request, response);
    }

    private @Nullable String extractToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(SecurityConstants.BEARER_PREFIX)) {
            return header.substring(SecurityConstants.BEARER_PREFIX.length());
        }

        Cookie cookie = WebUtils.getCookie(request, SecurityConstants.AUTH_COOKIE_NAME);
        return cookie != null && !cookie.getValue().isBlank() ? cookie.getValue() : null;
    }

    private void authenticate(TokenClaims claims, HttpServletRequest request) {
        try {
            MyUserDetails userDetails = myUserDetailsService.loadUserByUsername(claims.username());

            if (!userDetails.isEnabled() || !userDetails.isAccountNonLocked() || isRevoked(claims, userDetails)) {
                return;
            }

            UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken.authenticated(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (UsernameNotFoundException e) {
            log.warn(LogMessages.TOKEN_USER_NOT_FOUND, claims.username());
        }
    }

    private static boolean isRevoked(TokenClaims claims, MyUserDetails userDetails) {
        Instant changedAt = userDetails.user().getCredentialsChangedAt();
        return changedAt != null && claims.issuedAt().isBefore(changedAt.truncatedTo(ChronoUnit.SECONDS));
    }
}
