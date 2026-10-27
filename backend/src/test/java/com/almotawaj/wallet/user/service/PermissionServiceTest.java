package com.almotawaj.wallet.user.service;

import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.user.model.Permission;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.UserRole;
import com.almotawaj.wallet.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {
    private static final UUID ACTOR_ID = UUID.randomUUID();

    @Mock
    private UserRepository userRepository;
    @Mock
    private AdminUserMapper mapper;
    @Mock
    private AuditService auditService;

    @InjectMocks
    private PermissionService permissionService;

    private User userWithRole(UserRole role) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setRole(role);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        return user;
    }

    @Test
    void grant_addsPermissionToAdmin() {
        User admin = userWithRole(UserRole.ADMIN);

        permissionService.grant(ACTOR_ID, admin.getId(), Permission.KYC_REVIEW);

        assertThat(admin.getPermissions()).containsExactly(Permission.KYC_REVIEW);
    }

    @Test
    void grant_rejectsAdminPermissionForMerchant() {
        User merchant = userWithRole(UserRole.MERCHANT);

        assertThatThrownBy(() -> permissionService.grant(ACTOR_ID, merchant.getId(), Permission.KYC_REVIEW))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("code", ErrorCodes.PERMISSION_NOT_GRANTABLE);
        assertThat(merchant.getPermissions()).isEmpty();
    }

    @Test
    void grant_rejectsClient() {
        User client = userWithRole(UserRole.CLIENT);

        assertThatThrownBy(() -> permissionService.grant(ACTOR_ID, client.getId(), Permission.KYC_REVIEW))
                .hasFieldOrPropertyWithValue("code", ErrorCodes.PERMISSION_NOT_GRANTABLE);
    }

    @Test
    void revoke_rejectsSuperAdmin() {
        User superAdmin = userWithRole(UserRole.SUPER_ADMIN);

        assertThatThrownBy(() -> permissionService.revoke(ACTOR_ID, superAdmin.getId(), Permission.KYC_REVIEW))
                .hasFieldOrPropertyWithValue("code", ErrorCodes.SUPER_ADMIN_PERMISSIONS_FIXED);
    }

    @Test
    void revoke_removesPermission() {
        User admin = userWithRole(UserRole.ADMIN);
        admin.getPermissions().add(Permission.KYC_REVIEW);

        permissionService.revoke(ACTOR_ID, admin.getId(), Permission.KYC_REVIEW);

        assertThat(admin.getPermissions()).isEmpty();
    }
}
