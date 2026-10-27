package com.almotawaj.wallet.config.security;

import com.almotawaj.wallet.checkout.service.MerchantApiKeyService;
import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    private final MerchantApiKeyService apiKeyService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith(ApiPaths.GATEWAY);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String apiKey = request.getHeader(SecurityConstants.API_KEY_HEADER);
        if (apiKey != null && !apiKey.isBlank()) {
            apiKeyService.authenticate(apiKey.strip()).ifPresent(merchant ->
                    SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
                            new MyUserDetails(merchant), null,
                            List.of(new SimpleGrantedAuthority(SecurityConstants.GATEWAY_AUTHORITY)))));
        }
        filterChain.doFilter(request, response);
    }
}
