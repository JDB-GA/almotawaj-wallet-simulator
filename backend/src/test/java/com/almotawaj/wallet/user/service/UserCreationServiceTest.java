package com.almotawaj.wallet.user.service;

import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.auth.service.PasswordResetService;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.exception.InformationExistException;
import com.almotawaj.wallet.user.model.Permission;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.UserRole;
import com.almotawaj.wallet.user.model.request.CreateUserRequest;
import com.almotawaj.wallet.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCreationServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserManagementPolicy policy;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private PasswordResetService passwordResetService;
    @Mock
    private AdminUserMapper mapper;
    @Mock
    private AuditService auditService;

    private UserCreationService service;
    private User actor;

    @BeforeEach
    void setUp() {
        service = new UserCreationService(userRepository, policy, passwordEncoder, passwordResetService, mapper, auditService);
        actor = new User();
        actor.setRole(UserRole.SUPER_ADMIN);
    }

    @Test
    void create_savesUserAndSendsInvitation() {
        CreateUserRequest request = new CreateUserRequest("test@example.com", "33123456", UserRole.ADMIN, Set.of(Permission.USER_MANAGE));
        when(userRepository.existsByEmailAddress("test@example.com")).thenReturn(false);
        when(userRepository.existsByMobileNumber("+97333123456")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("unusable-hash");

        User savedUser = new User();
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(savedUser);

        service.create(actor, request);

        verify(policy).ensureCanCreate(actor, UserRole.ADMIN, Set.of(Permission.USER_MANAGE));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveAndFlush(userCaptor.capture());

        User captured = userCaptor.getValue();
        assertThat(captured.getEmailAddress()).isEqualTo("test@example.com");
        assertThat(captured.getMobileNumber()).isEqualTo("+97333123456");
        assertThat(captured.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(captured.getPassword()).isEqualTo("unusable-hash");
        assertThat(captured.getPermissions()).containsExactly(Permission.USER_MANAGE);

        verify(passwordResetService).sendInvitation(savedUser);
    }

    @Test
    void create_rejectsDuplicateEmail() {
        CreateUserRequest request = new CreateUserRequest("test@example.com", "33123456", UserRole.ADMIN, Set.of());
        when(userRepository.existsByEmailAddress("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.create(actor, request))
                .isInstanceOf(InformationExistException.class)
                .hasFieldOrPropertyWithValue("code", ErrorCodes.EMAIL_ALREADY_REGISTERED);

        verify(userRepository, never()).saveAndFlush(any());
        verify(passwordResetService, never()).sendInvitation(any());
    }

    @Test
    void create_rejectsDuplicateMobile() {
        CreateUserRequest request = new CreateUserRequest("test@example.com", "33123456", UserRole.ADMIN, Set.of());
        when(userRepository.existsByEmailAddress("test@example.com")).thenReturn(false);
        when(userRepository.existsByMobileNumber("+97333123456")).thenReturn(true);

        assertThatThrownBy(() -> service.create(actor, request))
                .isInstanceOf(InformationExistException.class)
                .hasFieldOrPropertyWithValue("code", ErrorCodes.MOBILE_ALREADY_REGISTERED);

        verify(userRepository, never()).saveAndFlush(any());
        verify(passwordResetService, never()).sendInvitation(any());
    }
}
