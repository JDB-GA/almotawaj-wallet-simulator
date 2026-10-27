package com.almotawaj.wallet.user.service;

import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.user.model.Permission;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.UserRole;
import com.almotawaj.wallet.user.model.UserStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UserManagementPolicy {
    public void ensureCanCreate(User actor, UserRole role, Set<Permission> permissions) {
        if (role == UserRole.SUPER_ADMIN) {
            throw new BusinessRuleException(ErrorMessages.ROLE_NOT_ASSIGNABLE, ErrorCodes.ROLE_NOT_ASSIGNABLE);
        }
        boolean isSuperAdmin = actor.getRole() == UserRole.SUPER_ADMIN;
        if (!isSuperAdmin && (role == UserRole.ADMIN || !permissions.isEmpty())) {
            throw new AccessDeniedException(ErrorMessages.ACCESS_DENIED);
        }
        if (permissions.stream().anyMatch(permission -> !permission.isGrantableTo(role))) {
            throw new BusinessRuleException(ErrorMessages.PERMISSION_NOT_GRANTABLE, ErrorCodes.PERMISSION_NOT_GRANTABLE);
        }
    }

    public void ensureCanManage(User actor, User target) {
        if (actor.getId().equals(target.getId())) {
            throw new BusinessRuleException(ErrorMessages.CANNOT_MANAGE_SELF, ErrorCodes.CANNOT_MANAGE_SELF);
        }
        boolean isSuperAdminTarget = target.getRole() == UserRole.SUPER_ADMIN;
        boolean isAdminTargetOfNonSuperAdmin = target.getRole() == UserRole.ADMIN && actor.getRole() != UserRole.SUPER_ADMIN;
        if (isSuperAdminTarget || isAdminTargetOfNonSuperAdmin) {
            throw new AccessDeniedException(ErrorMessages.ACCESS_DENIED);
        }
        if (target.getStatus() == UserStatus.CLOSED) {
            throw new BusinessRuleException(ErrorMessages.USER_CLOSED, ErrorCodes.USER_CLOSED);
        }
    }
}
