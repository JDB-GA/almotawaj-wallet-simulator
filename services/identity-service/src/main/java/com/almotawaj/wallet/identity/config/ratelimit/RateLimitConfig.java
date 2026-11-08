package com.almotawaj.wallet.identity.config.ratelimit;

import com.almotawaj.wallet.identity.config.constants.ApiPaths;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class RateLimitConfig implements WebMvcConfigurer {
    private final RateLimitInterceptor rateLimitInterceptor;
    private final WriteRateLimitInterceptor writeRateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns(ApiPaths.AUTH_LOGIN, ApiPaths.AUTH_REGISTER, ApiPaths.AUTH_VERIFY_EMAIL, ApiPaths.AUTH_RESEND_VERIFICATION,
                        ApiPaths.AUTH_FORGOT_PASSWORD, ApiPaths.AUTH_RESET_PASSWORD, ApiPaths.AUTH_CHANGE_PASSWORD);
        registry.addInterceptor(writeRateLimitInterceptor)
                .addPathPatterns(ApiPaths.PROFILE, ApiPaths.PROFILE + ApiPaths.ALL);
    }
}
