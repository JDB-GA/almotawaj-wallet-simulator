package com.almotawaj.wallet.auth.service;

import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.auth.model.OtpPurpose;
import com.almotawaj.wallet.auth.model.request.ResetPasswordRequest;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.email.service.CodeEmailService;
import com.almotawaj.wallet.exception.OtpVerificationException;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.UserStatus;
import com.almotawaj.wallet.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {
    private static final Instant NOW = Instant.parse("2026-10-05T08:00:00Z");
    private static final String EMAIL = "client@example.com";
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4);

    @Mock
    private UserRepository userRepository;
    @Mock
    private OtpService otpService;
    @Mock
    private CodeEmailService codeEmailService;
    @Mock
    private AuditService auditService;

    private PasswordResetService service;
    private User user;

    @BeforeEach
    void setUp() {
        service = new PasswordResetService(userRepository, otpService, codeEmailService, passwordEncoder,
                Clock.fixed(NOW, ZoneOffset.UTC), auditService);
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmailAddress(EMAIL);
    }

    @Test
    void requestReset_unknownEmailDoesNothingAndRevealsNothing() {
        when(userRepository.findByEmailAddress("ghost@example.com")).thenReturn(Optional.empty());

        service.requestReset(" Ghost@Example.com ", Locale.ENGLISH);

        verifyNoInteractions(otpService, codeEmailService);
    }

    @Test
    void requestReset_ignoresSuspendedUser() {
        user.setStatus(UserStatus.SUSPENDED);
        when(userRepository.findByEmailAddress(EMAIL)).thenReturn(Optional.of(user));

        service.requestReset(EMAIL, Locale.ENGLISH);

        verifyNoInteractions(otpService, codeEmailService);
    }

    @Test
    void reset_setsPasswordVerifiesEmailAndRevokesTokens() {
        when(userRepository.findByEmailAddress(EMAIL)).thenReturn(Optional.of(user));

        service.reset(new ResetPasswordRequest(EMAIL, "123456", "BrandNew123"));

        verify(otpService).verify(user.getId(), OtpPurpose.PASSWORD_RESET, "123456");
        assertThat(passwordEncoder.matches("BrandNew123", user.getPassword())).isTrue();
        assertThat(user.isEmailVerified()).isTrue();
        assertThat(user.getCredentialsChangedAt()).isEqualTo(NOW);
    }

    @Test
    void reset_unknownEmailLooksLikeWrongCode() {
        when(userRepository.findByEmailAddress(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.reset(new ResetPasswordRequest("ghost@example.com", "123456", "BrandNew123")))
                .isInstanceOf(OtpVerificationException.class)
                .hasFieldOrPropertyWithValue("code", ErrorCodes.OTP_INVALID);
    }
}
