package com.almotawaj.wallet.config.ratelimit;

import com.almotawaj.wallet.config.constants.ApiPaths;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class RateLimitConfig implements WebMvcConfigurer {
    private final RateLimitInterceptor rateLimitInterceptor;
    private final WriteRateLimitInterceptor writeRateLimitInterceptor;
    private final GatewayRateLimitInterceptor gatewayRateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns(ApiPaths.SEED, ApiPaths.WALLET_TOP_UPS, ApiPaths.WALLET_TRANSFERS, ApiPaths.WALLET_RECIPIENTS,
                        ApiPaths.WALLET_TRANSACTION_RECEIPTS, ApiPaths.WALLET_TRANSACTIONS_STATEMENT);
        registry.addInterceptor(writeRateLimitInterceptor)
                .addPathPatterns(ApiPaths.WALLET_PAYMENT_REQUESTS, ApiPaths.WALLET_PAYMENT_REQUESTS + ApiPaths.ALL,
                        ApiPaths.MERCHANT_API_KEYS, ApiPaths.MERCHANT_CHECKOUT_SESSIONS, ApiPaths.MERCHANT_VOUCHERS,
                        ApiPaths.MERCHANT_VOUCHERS + ApiPaths.ALL, ApiPaths.MERCHANT_PAYOUTS, ApiPaths.VOUCHERS + ApiPaths.ALL,
                        ApiPaths.MERCHANT_CHECKOUT_SESSIONS + ApiPaths.ALL, ApiPaths.MERCHANT_WEBHOOK,
                        ApiPaths.CHECKOUT + ApiPaths.ALL,
                        ApiPaths.PROFILE, ApiPaths.PROFILE + ApiPaths.ALL, ApiPaths.ADMIN_SIMULATIONS + ApiPaths.ALL);
        registry.addInterceptor(gatewayRateLimitInterceptor).addPathPatterns(ApiPaths.GATEWAY + ApiPaths.ALL);
    }
}
