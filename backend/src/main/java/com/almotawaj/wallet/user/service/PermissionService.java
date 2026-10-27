package com.almotawaj.wallet.user.service;

import com.almotawaj.wallet.audit.model.AuditAction;
import com.almotawaj.wallet.audit.model.AuditTargetType;
import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.LogMessages;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.exception.InformationNotFoundException;
import com.almotawaj.wallet.user.model.Permission;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.UserRole;
import com.almotawaj.wallet.user.model.response.AdminUserResponse;
import com.almotawaj.wallet.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {
    private final UserRepository userRepository;
    private final AdminUserMapper mapper;
    private final AuditService auditService;

    @Transactional
    public AdminUserResponse grant(UUID actorId, UUID userId, Permission permission) {
        User user = findEditableUser(userId);
        if (!permission.isGrantableTo(user.getRole())) {
            throw new BusinessRuleException(ErrorMessages.PERMISSION_NOT_GRANTABLE, ErrorCodes.PERMISSION_NOT_GRANTABLE);
        }
        if (user.getPermissions().add(permission)) {
            log.info(LogMessages.PERMISSION_GRANTED, actorId, permission, userId);
            auditService.record(actorId, AuditAction.PERMISSION_GRANTED, AuditTargetType.USER, userId, permission.name());
        }
        return mapper.toResponse(user);
    }

    @Transactional
    public AdminUserResponse revoke(UUID actorId, UUID userId, Permission permission) {
        User user = findEditableUser(userId);
        if (user.getPermissions().remove(permission)) {
            log.info(LogMessages.PERMISSION_REVOKED, actorId, permission, userId);
            auditService.record(actorId, AuditAction.PERMISSION_REVOKED, AuditTargetType.USER, userId, permission.name());
        }
        return mapper.toResponse(user);
    }

    private User findEditableUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InformationNotFoundException(ErrorMessages.USER_NOT_FOUND));
        if (user.getRole() == UserRole.SUPER_ADMIN) {
            throw new BusinessRuleException(ErrorMessages.SUPER_ADMIN_PERMISSIONS_FIXED, ErrorCodes.SUPER_ADMIN_PERMISSIONS_FIXED);
        }
        return user;
    }
}
