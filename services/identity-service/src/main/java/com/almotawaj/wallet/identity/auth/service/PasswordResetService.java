package com.almotawaj.wallet.identity.auth.service;

import com.almotawaj.wallet.identity.audit.AuditAction;
import com.almotawaj.wallet.identity.audit.AuditService;
import com.almotawaj.wallet.identity.audit.AuditTargetType;
import com.almotawaj.wallet.identity.auth.model.OtpPurpose;
import com.almotawaj.wallet.identity.auth.model.request.ResetPasswordRequest;
import com.almotawaj.wallet.identity.config.constants.ErrorCodes;
import com.almotawaj.wallet.identity.config.constants.ErrorMessages;
import com.almotawaj.wallet.identity.config.constants.KafkaConstants;
import com.almotawaj.wallet.identity.config.constants.LogMessages;
import com.almotawaj.wallet.identity.config.constants.OtpConstants;
import com.almotawaj.wallet.identity.event.EmailEvent;
import com.almotawaj.wallet.identity.event.EventPublisher;
import com.almotawaj.wallet.identity.exception.OtpVerificationException;
import com.almotawaj.wallet.identity.user.model.User;
import com.almotawaj.wallet.identity.user.model.UserStatus;
import com.almotawaj.wallet.identity.user.repository.UserRepository;
import com.almotawaj.wallet.identity.util.LoginIdentifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private static final OtpPurpose PURPOSE = OtpPurpose.PASSWORD_RESET;
    private static final Set<UserStatus> RESETTABLE_STATUSES = Set.of(UserStatus.ACTIVE, UserStatus.LOCKED);

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final EventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;
    private final AuditService auditService;

    @Transactional
    public void requestReset(String email, Locale locale) {
        findResettableUser(email).filter(user -> otpService.isResendAllowed(user.getId(), PURPOSE)).ifPresent(user -> {
            String code = otpService.issue(user, PURPOSE);
            eventPublisher.publish(KafkaConstants.EMAIL_EVENTS, user.getId().toString(), EmailEvent.code(
                    EmailEvent.EmailType.PASSWORD_RESET, user.getId(), user.getEmailAddress(), code, locale.toLanguageTag()));
            log.info(LogMessages.PASSWORD_RESET_REQUESTED, user.getId());
        });
    }

    @Transactional
    public void sendInvitation(User user) {
        String code = otpService.issue(user, PURPOSE, OtpConstants.INVITATION_EXPIRY);
        eventPublisher.publish(KafkaConstants.EMAIL_EVENTS, user.getId().toString(), EmailEvent.code(
                EmailEvent.EmailType.INVITATION, user.getId(), user.getEmailAddress(), code, null));
    }

    @Transactional(noRollbackFor = OtpVerificationException.class)
    public void reset(ResetPasswordRequest request) {
        User user = findResettableUser(request.email())
                .orElseThrow(() -> new OtpVerificationException(ErrorMessages.OTP_INVALID, ErrorCodes.OTP_INVALID));
        otpService.verify(user.getId(), PURPOSE, request.code());

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setEmailVerified(true);
        user.setCredentialsChangedAt(clock.instant());
        log.info(LogMessages.PASSWORD_RESET, user.getId());
        auditService.record(user.getId(), AuditAction.PASSWORD_RESET, AuditTargetType.USER, user.getId(), null);
    }

    private Optional<User> findResettableUser(String email) {
        return userRepository.findByEmailAddress(LoginIdentifier.normalizeEmail(email))
                .filter(user -> RESETTABLE_STATUSES.contains(user.getStatus()));
    }
}
