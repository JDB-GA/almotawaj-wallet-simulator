package com.almotawaj.wallet.config.ratelimit;

import com.almotawaj.wallet.config.constants.RateLimitConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class WriteRateLimitInterceptor extends RateLimitInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        return HttpMethod.GET.matches(request.getMethod()) || super.preHandle(request, response, handler);
    }

    @Override
    protected String keyOf(HttpServletRequest request) {
        Principal user = request.getUserPrincipal();
        return user == null ? super.keyOf(request) : request.getRequestURI() + RateLimitConstants.KEY_SEPARATOR + user.getName();
    }
}
