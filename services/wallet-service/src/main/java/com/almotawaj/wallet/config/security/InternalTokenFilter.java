package com.almotawaj.wallet.config.security;

import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class InternalTokenFilter extends OncePerRequestFilter {
    private final byte[] expectedToken;

    public InternalTokenFilter(@Value(SecurityConstants.INTERNAL_TOKEN_PROPERTY) String token) {
        this.expectedToken = token.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith(ApiPaths.INTERNAL_PREFIX);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = request.getHeader(SecurityConstants.INTERNAL_TOKEN_HEADER);
        if (token == null || expectedToken.length == 0
                || !MessageDigest.isEqual(expectedToken, token.getBytes(StandardCharsets.UTF_8))) {
            response.sendError(HttpStatus.UNAUTHORIZED.value());
            return;
        }
        chain.doFilter(request, response);
    }
}
