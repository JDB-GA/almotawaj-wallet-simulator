package com.almotawaj.wallet.user.service;

import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.exception.InformationExistException;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.UserRole;
import com.almotawaj.wallet.user.model.UserStatus;
import com.almotawaj.wallet.user.model.request.UpdateUserContactRequest;
import com.almotawaj.wallet.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private AdminUserMapper mapper;
    @Mock
    private AuditService auditService;

    private UserManagementService service;
    private User actor;
    private User target;

    @BeforeEach
    void setUp() {
        service = new UserManagementService(userRepository, new UserManagementPolicy(), mapper,
                new UserContactUpdater(userRepository), auditService);
        actor = new User();
        actor.setId(UUID.randomUUID());
        actor.setRole(UserRole.ADMIN);
        target = new User();
        target.setId(UUID.randomUUID());
        target.setEmailAddress("client@example.com");
        target.setEmailVerified(true);
        when(userRepository.findByIdForUpdate(target.getId())).thenReturn(Optional.of(target));
    }

    @Test
    void suspendThenReactivate() {
        service.suspend(actor, target.getId());
        assertThat(target.getStatus()).isEqualTo(UserStatus.SUSPENDED);

        service.reactivate(actor, target.getId());
        assertThat(target.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void reactivateRejectsActiveUser() {
        assertThatThrownBy(() -> service.reactivate(actor, target.getId()))
                .hasFieldOrPropertyWithValue("code", ErrorCodes.INVALID_STATUS_TRANSITION);
    }

    @Test
    void closeSoftDeletesUser() {
        service.close(actor, target.getId());

        assertThat(target.getStatus()).isEqualTo(UserStatus.CLOSED);
    }

    @Test
    void changingEmailMarksItUnverified() {
        when(userRepository.existsByEmailAddress("new@example.com")).thenReturn(false);

        service.updateContact(actor, target.getId(), new UpdateUserContactRequest(" New@Example.com ", null));

        assertThat(target.getEmailAddress()).isEqualTo("new@example.com");
        assertThat(target.isEmailVerified()).isFalse();
    }

    @Test
    void changingEmailRejectsTakenAddress() {
        when(userRepository.existsByEmailAddress("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.updateContact(actor, target.getId(), new UpdateUserContactRequest("taken@example.com", null)))
                .isInstanceOf(InformationExistException.class)
                .hasFieldOrPropertyWithValue("code", ErrorCodes.EMAIL_ALREADY_REGISTERED);
        assertThat(target.getEmailAddress()).isEqualTo("client@example.com");
    }
}
