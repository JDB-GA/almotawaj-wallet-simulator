package com.almotawaj.wallet.config.security;

import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.header.writers.DelegatingRequestMatcherHeaderWriter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class SecurityHeadersCustomizer implements Customizer<HeadersConfigurer<HttpSecurity>> {
    private static final RequestMatcher DOCS = new OrRequestMatcher(
            PathPatternRequestMatcher.withDefaults().matcher(ApiPaths.SWAGGER_UI_HTML),
            PathPatternRequestMatcher.withDefaults().matcher(ApiPaths.SWAGGER_UI),
            PathPatternRequestMatcher.withDefaults().matcher(ApiPaths.API_DOCS));

    @Override
    public void customize(HeadersConfigurer<HttpSecurity> headers) {
        headers
                .addHeaderWriter(contentSecurityPolicy(DOCS, SecurityConstants.DOCS_CONTENT_SECURITY_POLICY))
                .addHeaderWriter(contentSecurityPolicy(new NegatedRequestMatcher(DOCS), SecurityConstants.CONTENT_SECURITY_POLICY))
                .referrerPolicy(referrer -> referrer.policy(ReferrerPolicy.NO_REFERRER))
                .permissionsPolicyHeader(permissions -> permissions.policy(SecurityConstants.PERMISSIONS_POLICY))
                .httpStrictTransportSecurity(hsts -> hsts
                        .requestMatcher(AnyRequestMatcher.INSTANCE)
                        .includeSubDomains(true)
                        .maxAgeInSeconds(SecurityConstants.HSTS_MAX_AGE_SECONDS));
    }

    private static DelegatingRequestMatcherHeaderWriter contentSecurityPolicy(RequestMatcher matcher, String policy) {
        return new DelegatingRequestMatcherHeaderWriter(matcher,
                new StaticHeadersWriter(SecurityConstants.CONTENT_SECURITY_POLICY_HEADER, policy));
    }
}
