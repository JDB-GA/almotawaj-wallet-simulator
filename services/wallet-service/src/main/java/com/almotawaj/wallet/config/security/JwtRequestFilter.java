package com.almotawaj.wallet.config.security;

import com.almotawaj.wallet.account.service.AccountHolderService;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;

/**
 * Trusts the token from identity-service and loads wallet's copy of the account.
 */
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final AccountHolderService accountHolderService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
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
        accountHolderService.findOrFetch(claims.userId()).ifPresent(account -> {
            MyUserDetails userDetails = new MyUserDetails(account,
                    claims.authorities().stream().map(SimpleGrantedAuthority::new).toList());
            if (!userDetails.isEnabled() || !userDetails.isAccountNonLocked()) {
                return;
            }
            var authentication = UsernamePasswordAuthenticationToken.authenticated(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        });
    }
}
