package com.almotawaj.wallet.user.service;

import com.almotawaj.wallet.audit.model.AuditAction;
import com.almotawaj.wallet.audit.model.AuditTargetType;
import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.auth.service.PasswordResetService;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.LogMessages;
import com.almotawaj.wallet.exception.InformationExistException;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.request.CreateUserRequest;
import com.almotawaj.wallet.user.model.response.AdminUserResponse;
import com.almotawaj.wallet.user.repository.UserRepository;
import com.almotawaj.wallet.util.LoginIdentifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCreationService {
    private static final int UNUSABLE_PASSWORD_BYTES = 32;

    private final UserRepository userRepository;
    private final UserManagementPolicy policy;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetService passwordResetService;
    private final AdminUserMapper mapper;
    private final AuditService auditService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public AdminUserResponse create(User actor, CreateUserRequest request) {
        policy.ensureCanCreate(actor, request.role(), request.permissionsOrEmpty());
        String email = LoginIdentifier.normalizeEmail(request.email());
        String mobileNumber = LoginIdentifier.normalizeMobile(request.mobileNumber());
        if (userRepository.existsByEmailAddress(email)) {
            throw new InformationExistException(ErrorMessages.EMAIL_ALREADY_REGISTERED, ErrorCodes.EMAIL_ALREADY_REGISTERED);
        }
        if (userRepository.existsByMobileNumber(mobileNumber)) {
            throw new InformationExistException(ErrorMessages.MOBILE_ALREADY_REGISTERED, ErrorCodes.MOBILE_ALREADY_REGISTERED);
        }

        User user = new User();
        user.setEmailAddress(email);
        user.setMobileNumber(mobileNumber);
        user.setRole(request.role());
        user.setPassword(passwordEncoder.encode(unusablePassword()));
        user.getPermissions().addAll(request.permissionsOrEmpty());
        User saved = userRepository.saveAndFlush(user);

        passwordResetService.sendInvitation(saved);
        log.info(LogMessages.USER_CREATED, actor.getId(), saved.getId(), saved.getRole());
        auditService.record(actor.getId(), AuditAction.USER_CREATED, AuditTargetType.USER, saved.getId(), saved.getRole().name());
        return mapper.toResponse(saved);
    }

    private String unusablePassword() {
        byte[] bytes = new byte[UNUSABLE_PASSWORD_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
