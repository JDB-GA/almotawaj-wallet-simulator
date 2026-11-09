package com.almotawaj.wallet.identity.config.security;

import com.almotawaj.wallet.identity.config.constants.ApiPaths;
import com.almotawaj.wallet.identity.config.constants.ErrorMessages;
import com.almotawaj.wallet.identity.config.constants.InternalConstants;
import com.almotawaj.wallet.identity.config.constants.SecurityConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(new SecurityHeadersCustomizer())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, ApiPaths.AUTH_REGISTER, ApiPaths.AUTH_LOGIN, ApiPaths.AUTH_LOGOUT,
                                ApiPaths.AUTH_FORGOT_PASSWORD, ApiPaths.AUTH_RESET_PASSWORD).permitAll()
                        .requestMatchers(ApiPaths.ERROR, ApiPaths.SEED, ApiPaths.HEALTH, InternalConstants.INTERNAL_PATHS).permitAll()
                        .requestMatchers(HttpMethod.GET, ApiPaths.SWAGGER_UI_HTML, ApiPaths.SWAGGER_UI, ApiPaths.API_DOCS).permitAll()
                        .requestMatchers(HttpMethod.GET, ApiPaths.AUTH_ME).authenticated()
                        .requestMatchers(HttpMethod.POST, ApiPaths.AUTH_VERIFY_EMAIL, ApiPaths.AUTH_RESEND_VERIFICATION,
                                ApiPaths.AUTH_CHANGE_PASSWORD).authenticated()
                        .anyRequest().hasAuthority(SecurityConstants.EMAIL_VERIFIED_AUTHORITY))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Checks account status only after the password matches, so a suspended or locked account
     * gets a specific error without revealing account state to someone who doesn't know the password.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(MyUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setPreAuthenticationChecks(user -> { });
        provider.setPostAuthenticationChecks(user -> {
            if (!user.isAccountNonLocked()) {
                throw new LockedException(ErrorMessages.ACCOUNT_LOCKED);
            }
            if (!user.isEnabled()) {
                throw new DisabledException(ErrorMessages.ACCOUNT_DEACTIVATED);
            }
        });
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) {
        return authConfig.getAuthenticationManager();
    }
}
