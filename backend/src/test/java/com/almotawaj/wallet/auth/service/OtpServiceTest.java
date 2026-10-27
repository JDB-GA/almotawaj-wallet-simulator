package com.almotawaj.wallet.auth.service;

import com.almotawaj.wallet.auth.model.OtpChallenge;
import com.almotawaj.wallet.auth.model.OtpPurpose;
import com.almotawaj.wallet.auth.repository.OtpChallengeRepository;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.OtpConstants;
import com.almotawaj.wallet.config.security.OtpHasher;
import com.almotawaj.wallet.exception.OtpResendCooldownException;
import com.almotawaj.wallet.exception.OtpVerificationException;
import com.almotawaj.wallet.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {
    private static final OtpPurpose PURPOSE = OtpPurpose.EMAIL_VERIFICATION;
    private static final Instant NOW = Instant.parse("2026-10-01T10:00:00Z");

    @Mock
    private OtpChallengeRepository repository;

    private final OtpHasher otpHasher = new OtpHasher("test-otp-secret-that-is-long-enough-123456");
    private final User user = new User();
    private OtpService otpService;

    @BeforeEach
    void setUp() {
        user.setId(UUID.randomUUID());
        otpService = new OtpService(repository, otpHasher, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void issue_replacesOldCodeAndStoresOnlyTheHash() {
        String code = otpService.issue(user, PURPOSE);

        ArgumentCaptor<OtpChallenge> captor = ArgumentCaptor.forClass(OtpChallenge.class);
        verify(repository).deleteByUser_IdAndPurpose(user.getId(), PURPOSE);
        verify(repository).save(captor.capture());
        assertThat(code).matches("\\d{6}");
        assertThat(captor.getValue().getCodeHash()).isEqualTo(otpHasher.hash(user.getId(), PURPOSE, code));
        assertThat(captor.getValue().getExpiresAt()).isEqualTo(NOW.plus(OtpConstants.EXPIRY));
    }

    @Test
    void verify_correctCode_consumesChallenge() {
        OtpChallenge challenge = stubChallenge("123456", NOW.plusSeconds(60), 0);

        otpService.verify(user.getId(), PURPOSE, "123456");

        assertThat(challenge.getConsumedAt()).isEqualTo(NOW);
    }

    @Test
    void verify_wrongCode_countsAttemptAndRejects() {
        OtpChallenge challenge = stubChallenge("123456", NOW.plusSeconds(60), 0);

        assertRejected("000000", ErrorCodes.OTP_INVALID);
        assertThat(challenge.getAttempts()).isEqualTo(1);
        assertThat(challenge.getConsumedAt()).isNull();
    }

    @Test
    void verify_expiredOrExhaustedOrMissing_rejects() {
        stubChallenge("123456", NOW, 0);
        assertRejected("123456", ErrorCodes.OTP_EXPIRED);

        stubChallenge("123456", NOW.plusSeconds(60), OtpConstants.MAX_ATTEMPTS);
        assertRejected("123456", ErrorCodes.OTP_ATTEMPTS_EXCEEDED);

        when(repository.findFirstByUser_IdAndPurposeOrderByCreatedAtDesc(user.getId(), PURPOSE)).thenReturn(Optional.empty());
        assertRejected("123456", ErrorCodes.OTP_NOT_FOUND);
    }

    @Test
    void ensureResendAllowed_rejectsWithinCooldown() {
        OtpChallenge challenge = stubChallenge("123456", NOW.plusSeconds(600), 0);
        challenge.setCreatedAt(NOW.minusSeconds(10));

        assertThatThrownBy(() -> otpService.ensureResendAllowed(user.getId(), PURPOSE))
                .isInstanceOf(OtpResendCooldownException.class);
    }

    private OtpChallenge stubChallenge(String code, Instant expiresAt, int attempts) {
        OtpChallenge challenge = new OtpChallenge();
        challenge.setCodeHash(otpHasher.hash(user.getId(), PURPOSE, code));
        challenge.setExpiresAt(expiresAt);
        challenge.setAttempts(attempts);
        challenge.setCreatedAt(NOW.minusSeconds(120));
        when(repository.findFirstByUser_IdAndPurposeOrderByCreatedAtDesc(user.getId(), PURPOSE)).thenReturn(Optional.of(challenge));
        return challenge;
    }

    private void assertRejected(String code, String expectedCode) {
        assertThatThrownBy(() -> otpService.verify(user.getId(), PURPOSE, code))
                .isInstanceOfSatisfying(OtpVerificationException.class, e -> assertThat(e.getCode()).isEqualTo(expectedCode));
    }
}
