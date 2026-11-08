package com.almotawaj.wallet.config.security;

import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtRequestFilter jwtRequestFilter;
    private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .headers(new SecurityHeadersCustomizer())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ApiPaths.ERROR, ApiPaths.SEED, ApiPaths.HEALTH).permitAll()
                        .requestMatchers(HttpMethod.GET, ApiPaths.SWAGGER_UI_HTML, ApiPaths.SWAGGER_UI, ApiPaths.API_DOCS).permitAll()
                        .requestMatchers(ApiPaths.GATEWAY + ApiPaths.ALL).hasAuthority(SecurityConstants.GATEWAY_AUTHORITY)
                        .anyRequest().hasAuthority(SecurityConstants.EMAIL_VERIFIED_AUTHORITY))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(apiKeyAuthenticationFilter, JwtRequestFilter.class);

        return http.build();
    }

}
