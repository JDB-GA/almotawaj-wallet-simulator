package com.almotawaj.wallet.identity.auth.service;

import com.almotawaj.wallet.identity.audit.AuditAction;
import com.almotawaj.wallet.identity.audit.AuditTargetType;
import com.almotawaj.wallet.identity.audit.AuditService;
import com.almotawaj.wallet.identity.auth.model.OtpPurpose;
import com.almotawaj.wallet.identity.config.constants.ErrorCodes;
import com.almotawaj.wallet.identity.config.constants.ErrorMessages;
import com.almotawaj.wallet.identity.email.service.EmailService;
import com.almotawaj.wallet.identity.exception.InformationExistException;
import com.almotawaj.wallet.identity.exception.InformationNotFoundException;
import com.almotawaj.wallet.identity.exception.OtpVerificationException;
import com.almotawaj.wallet.identity.user.model.User;
import com.almotawaj.wallet.identity.user.model.response.UserResponse;
import com.almotawaj.wallet.identity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private static final OtpPurpose PURPOSE = OtpPurpose.EMAIL_VERIFICATION;

    private final OtpService otpService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final AuditService auditService;

    @Transactional
    public void sendCode(User user, Locale locale) {
        String code = otpService.issue(user, PURPOSE);
        emailService.sendVerificationCode(user.getId(), user.getEmailAddress(), code, locale);
    }

    @Transactional
    public void sendCodeIfNoneActive(User user, Locale locale) {
        if (!user.isEmailVerified() && !otpService.hasUsableChallenge(user.getId(), PURPOSE)) {
            sendCode(user, locale);
        }
    }

    @Transactional
    public void resend(UUID userId, Locale locale) {
        User user = findUnverifiedUser(userId);
        otpService.ensureResendAllowed(userId, PURPOSE);
        sendCode(user, locale);
    }

    @Transactional(noRollbackFor = OtpVerificationException.class)
    public UserResponse verify(UUID userId, String code) {
        User user = findUnverifiedUser(userId);
        otpService.verify(userId, PURPOSE, code);
        user.setEmailVerified(true);
        auditService.record(userId, AuditAction.EMAIL_VERIFIED, AuditTargetType.USER, userId, null);
        return UserResponse.from(user);
    }

    private User findUnverifiedUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InformationNotFoundException(ErrorMessages.USER_NOT_FOUND));
        if (user.isEmailVerified()) {
            throw new InformationExistException(ErrorMessages.EMAIL_ALREADY_VERIFIED, ErrorCodes.EMAIL_ALREADY_VERIFIED);
        }
        return user;
    }
}
