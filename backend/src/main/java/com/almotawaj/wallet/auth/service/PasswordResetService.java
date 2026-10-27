package com.almotawaj.wallet.auth.service;

import com.almotawaj.wallet.audit.model.AuditAction;
import com.almotawaj.wallet.audit.model.AuditTargetType;
import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.auth.model.OtpPurpose;
import com.almotawaj.wallet.auth.model.request.ResetPasswordRequest;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.LogMessages;
import com.almotawaj.wallet.config.constants.OtpConstants;
import com.almotawaj.wallet.email.service.CodeEmailService;
import com.almotawaj.wallet.exception.OtpVerificationException;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.UserStatus;
import com.almotawaj.wallet.user.repository.UserRepository;
import com.almotawaj.wallet.util.LoginIdentifier;
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
    private final CodeEmailService codeEmailService;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;
    private final AuditService auditService;

    @Transactional
    public void requestReset(String email, Locale locale) {
        findResettableUser(email).filter(user -> otpService.isResendAllowed(user.getId(), PURPOSE)).ifPresent(user -> {
            String code = otpService.issue(user, PURPOSE);
            codeEmailService.sendPasswordReset(user.getId(), user.getEmailAddress(), code, locale);
            log.info(LogMessages.PASSWORD_RESET_REQUESTED, user.getId());
        });
    }

    @Transactional
    public void sendInvitation(User user) {
        String code = otpService.issue(user, PURPOSE, OtpConstants.INVITATION_EXPIRY);
        codeEmailService.sendInvitation(user.getId(), user.getEmailAddress(), code);
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
