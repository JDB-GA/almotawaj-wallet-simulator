package com.almotawaj.wallet.config.ratelimit;

import com.almotawaj.wallet.config.constants.GatewayConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class GatewayRateLimitInterceptor extends RateLimitInterceptor {
    @Override
    protected String keyOf(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        return principal == null ? request.getRemoteAddr() : principal.getName();
    }

    @Override
    protected long capacity() {
        return GatewayConstants.REQUESTS_PER_MINUTE;
    }
}
