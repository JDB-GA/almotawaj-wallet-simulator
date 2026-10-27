package com.almotawaj.wallet.user.service;

import com.almotawaj.wallet.audit.model.AuditAction;
import com.almotawaj.wallet.audit.model.AuditTargetType;
import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.common.model.response.PageResponse;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.LogMessages;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.exception.InformationNotFoundException;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.UserRole;
import com.almotawaj.wallet.user.model.UserStatus;
import com.almotawaj.wallet.user.model.request.UpdateUserContactRequest;
import com.almotawaj.wallet.user.model.response.AdminUserResponse;
import com.almotawaj.wallet.user.model.response.AdminUserSummaryResponse;
import com.almotawaj.wallet.user.repository.UserRepository;
import com.almotawaj.wallet.user.repository.specification.UserSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementService {
    private final UserRepository userRepository;
    private final UserManagementPolicy policy;
    private final AdminUserMapper mapper;
    private final UserContactUpdater contactUpdater;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public PageResponse<AdminUserSummaryResponse> list(String search, UserRole role, UserStatus status, Pageable pageable) {
        return PageResponse.from(userRepository.findAll(UserSpecifications.matches(search, role, status), pageable),
                AdminUserSummaryResponse::from);
    }

    @Transactional(readOnly = true)
    public AdminUserResponse get(UUID userId) {
        return mapper.toResponse(userRepository.findById(userId)
                .orElseThrow(() -> new InformationNotFoundException(ErrorMessages.USER_NOT_FOUND)));
    }

    @Transactional
    public AdminUserResponse updateContact(User actor, UUID userId, UpdateUserContactRequest request) {
        User target = findManageableUser(actor, userId);
        contactUpdater.apply(target, request);
        log.info(LogMessages.USER_CONTACT_UPDATED, actor.getId(), userId);
        auditService.record(actor.getId(), AuditAction.USER_CONTACT_UPDATED, AuditTargetType.USER, userId, null);
        return mapper.toResponse(target);
    }

    @Transactional
    public AdminUserResponse suspend(User actor, UUID userId) {
        return changeStatus(actor, userId, Set.of(UserStatus.ACTIVE, UserStatus.LOCKED), UserStatus.SUSPENDED,
                AuditAction.USER_SUSPENDED);
    }

    @Transactional
    public AdminUserResponse reactivate(User actor, UUID userId) {
        return changeStatus(actor, userId, Set.of(UserStatus.SUSPENDED, UserStatus.LOCKED), UserStatus.ACTIVE,
                AuditAction.USER_REACTIVATED);
    }

    @Transactional
    public void close(User actor, UUID userId) {
        changeStatus(actor, userId, Set.of(UserStatus.ACTIVE, UserStatus.SUSPENDED, UserStatus.LOCKED), UserStatus.CLOSED,
                AuditAction.USER_CLOSED);
    }

    private AdminUserResponse changeStatus(User actor, UUID userId, Set<UserStatus> allowedFrom, UserStatus next,
                                           AuditAction action) {
        User target = findManageableUser(actor, userId);
        UserStatus previous = target.getStatus();
        if (!allowedFrom.contains(previous)) {
            throw new BusinessRuleException(ErrorMessages.INVALID_STATUS_TRANSITION, ErrorCodes.INVALID_STATUS_TRANSITION);
        }
        target.setStatus(next);
        log.info(LogMessages.USER_STATUS_CHANGED, actor.getId(), userId, previous, next);
        auditService.record(actor.getId(), action, AuditTargetType.USER, userId, previous + " -> " + next);
        return mapper.toResponse(target);
    }

    private User findManageableUser(User actor, UUID userId) {
        User target = userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new InformationNotFoundException(ErrorMessages.USER_NOT_FOUND));
        policy.ensureCanManage(actor, target);
        return target;
    }
}
