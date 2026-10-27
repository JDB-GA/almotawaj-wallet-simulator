package com.almotawaj.wallet.auth.service;

import com.almotawaj.wallet.auth.model.OtpChallenge;
import com.almotawaj.wallet.auth.model.OtpPurpose;
import com.almotawaj.wallet.auth.repository.OtpChallengeRepository;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.OtpConstants;
import com.almotawaj.wallet.config.security.OtpHasher;
import com.almotawaj.wallet.exception.OtpResendCooldownException;
import com.almotawaj.wallet.exception.OtpVerificationException;
import com.almotawaj.wallet.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final OtpChallengeRepository otpChallengeRepository;
    private final OtpHasher otpHasher;
    private final Clock clock;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public String issue(User user, OtpPurpose purpose) {
        return issue(user, purpose, OtpConstants.EXPIRY);
    }

    @Transactional
    public String issue(User user, OtpPurpose purpose, Duration expiry) {
        otpChallengeRepository.deleteByUser_IdAndPurpose(user.getId(), purpose);

        String code = String.format(OtpConstants.CODE_FORMAT, secureRandom.nextInt(OtpConstants.CODE_BOUND));
        Instant now = clock.instant();

        OtpChallenge challenge = new OtpChallenge();
        challenge.setUser(user);
        challenge.setPurpose(purpose);
        challenge.setCodeHash(otpHasher.hash(user.getId(), purpose, code));
        challenge.setCreatedAt(now);
        challenge.setExpiresAt(now.plus(expiry));
        otpChallengeRepository.save(challenge);

        return code;
    }

    @Transactional(readOnly = true)
    public boolean hasUsableChallenge(UUID userId, OtpPurpose purpose) {
        Instant now = clock.instant();
        return otpChallengeRepository.findFirstByUser_IdAndPurposeOrderByCreatedAtDesc(userId, purpose)
                .filter(challenge -> challenge.getConsumedAt() == null)
                .filter(challenge -> challenge.getExpiresAt().isAfter(now))
                .filter(challenge -> challenge.getAttempts() < OtpConstants.MAX_ATTEMPTS)
                .isPresent();
    }

    @Transactional(readOnly = true)
    public void ensureResendAllowed(UUID userId, OtpPurpose purpose) {
        Duration remaining = remainingCooldown(userId, purpose);
        if (isPositive(remaining)) {
            throw new OtpResendCooldownException(remaining);
        }
    }

    @Transactional(readOnly = true)
    public boolean isResendAllowed(UUID userId, OtpPurpose purpose) {
        return !isPositive(remainingCooldown(userId, purpose));
    }

    private static boolean isPositive(Duration duration) {
        return duration.compareTo(Duration.ZERO) > 0;
    }

    private Duration remainingCooldown(UUID userId, OtpPurpose purpose) {
        return otpChallengeRepository.findFirstByUser_IdAndPurposeOrderByCreatedAtDesc(userId, purpose)
                .map(challenge -> Duration.between(clock.instant(), challenge.getCreatedAt().plus(OtpConstants.RESEND_COOLDOWN)))
                .orElse(Duration.ZERO);
    }

    @Transactional(noRollbackFor = OtpVerificationException.class)
    public void verify(UUID userId, OtpPurpose purpose, String code) {
        OtpChallenge challenge = otpChallengeRepository.findFirstByUser_IdAndPurposeOrderByCreatedAtDesc(userId, purpose)
                .filter(candidate -> candidate.getConsumedAt() == null)
                .orElseThrow(() -> new OtpVerificationException(ErrorMessages.OTP_NOT_FOUND, ErrorCodes.OTP_NOT_FOUND));

        Instant now = clock.instant();
        if (!challenge.getExpiresAt().isAfter(now)) {
            throw new OtpVerificationException(ErrorMessages.OTP_EXPIRED, ErrorCodes.OTP_EXPIRED);
        }
        if (challenge.getAttempts() >= OtpConstants.MAX_ATTEMPTS) {
            throw new OtpVerificationException(ErrorMessages.OTP_ATTEMPTS_EXCEEDED, ErrorCodes.OTP_ATTEMPTS_EXCEEDED);
        }
        if (!otpHasher.matches(userId, purpose, code, challenge.getCodeHash())) {
            challenge.setAttempts(challenge.getAttempts() + 1);
            throw new OtpVerificationException(ErrorMessages.OTP_INVALID, ErrorCodes.OTP_INVALID);
        }

        challenge.setConsumedAt(now);
    }
}
