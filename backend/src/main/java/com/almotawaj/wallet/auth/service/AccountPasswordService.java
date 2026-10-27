package com.almotawaj.wallet.auth.service;

import com.almotawaj.wallet.audit.model.AuditAction;
import com.almotawaj.wallet.audit.model.AuditTargetType;
import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.auth.model.LoginResult;
import com.almotawaj.wallet.auth.model.request.ChangePasswordRequest;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.LogMessages;
import com.almotawaj.wallet.config.security.JwtUtils;
import com.almotawaj.wallet.config.security.MyUserDetails;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.exception.InformationNotFoundException;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.response.UserResponse;
import com.almotawaj.wallet.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountPasswordService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final Clock clock;
    private final AuditService auditService;

    @Transactional
    public LoginResult changePassword(UUID userId, ChangePasswordRequest request) {
        User user = userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new InformationNotFoundException(ErrorMessages.USER_NOT_FOUND));
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BusinessRuleException(ErrorMessages.INVALID_CURRENT_PASSWORD, ErrorCodes.INVALID_CURRENT_PASSWORD);
        }
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new BusinessRuleException(ErrorMessages.PASSWORD_REUSED, ErrorCodes.PASSWORD_REUSED);
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setCredentialsChangedAt(clock.instant());
        log.info(LogMessages.PASSWORD_CHANGED, userId);
        auditService.record(userId, AuditAction.PASSWORD_CHANGED, AuditTargetType.USER, userId, null);
        return new LoginResult(jwtUtils.generateToken(new MyUserDetails(user)), UserResponse.from(user));
    }
}
