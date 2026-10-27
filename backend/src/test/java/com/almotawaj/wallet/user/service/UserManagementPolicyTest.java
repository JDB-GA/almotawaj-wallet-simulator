package com.almotawaj.wallet.user.service;

import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.user.model.Permission;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.UserRole;
import com.almotawaj.wallet.user.model.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserManagementPolicyTest {
    private final UserManagementPolicy policy = new UserManagementPolicy();

    private static User user(UserRole role) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setRole(role);
        return user;
    }

    @Test
    void adminCanManageClientsAndMerchants() {
        User admin = user(UserRole.ADMIN);

        assertThatCode(() -> policy.ensureCanManage(admin, user(UserRole.CLIENT))).doesNotThrowAnyException();
        assertThatCode(() -> policy.ensureCanManage(admin, user(UserRole.MERCHANT))).doesNotThrowAnyException();
    }

    @Test
    void onlySuperAdminCanManageAdmins() {
        User target = user(UserRole.ADMIN);

        assertThatThrownBy(() -> policy.ensureCanManage(user(UserRole.ADMIN), target)).isInstanceOf(AccessDeniedException.class);
        assertThatCode(() -> policy.ensureCanManage(user(UserRole.SUPER_ADMIN), target)).doesNotThrowAnyException();
    }

    @Test
    void nobodyCanManageSuperAdmin() {
        assertThatThrownBy(() -> policy.ensureCanManage(user(UserRole.SUPER_ADMIN), user(UserRole.SUPER_ADMIN)))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void cannotManageSelf() {
        User admin = user(UserRole.SUPER_ADMIN);

        assertThatThrownBy(() -> policy.ensureCanManage(admin, admin))
                .hasFieldOrPropertyWithValue("code", ErrorCodes.CANNOT_MANAGE_SELF);
    }

    @Test
    void closedAccountsAreReadOnly() {
        User target = user(UserRole.CLIENT);
        target.setStatus(UserStatus.CLOSED);

        assertThatThrownBy(() -> policy.ensureCanManage(user(UserRole.ADMIN), target))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("code", ErrorCodes.USER_CLOSED);
    }

    @Test
    void createRules() {
        User admin = user(UserRole.ADMIN);
        User superAdmin = user(UserRole.SUPER_ADMIN);

        assertThatCode(() -> policy.ensureCanCreate(admin, UserRole.MERCHANT, Set.of())).doesNotThrowAnyException();
        assertThatThrownBy(() -> policy.ensureCanCreate(admin, UserRole.ADMIN, Set.of())).isInstanceOf(AccessDeniedException.class);
        assertThatCode(() -> policy.ensureCanCreate(superAdmin, UserRole.ADMIN, Set.of(Permission.KYC_REVIEW))).doesNotThrowAnyException();
        assertThatThrownBy(() -> policy.ensureCanCreate(superAdmin, UserRole.SUPER_ADMIN, Set.of()))
                .hasFieldOrPropertyWithValue("code", ErrorCodes.ROLE_NOT_ASSIGNABLE);
        assertThatThrownBy(() -> policy.ensureCanCreate(superAdmin, UserRole.CLIENT, Set.of(Permission.USER_MANAGE)))
                .hasFieldOrPropertyWithValue("code", ErrorCodes.PERMISSION_NOT_GRANTABLE);
    }
}
