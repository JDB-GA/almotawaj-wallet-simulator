package com.almotawaj.wallet.auth.service;

import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.auth.model.request.ChangePasswordRequest;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.security.JwtUtils;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountPasswordServiceTest {
    private static final Instant NOW = Instant.parse("2026-10-05T08:00:00Z");
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4);

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private AuditService auditService;

    private AccountPasswordService service;
    private User user;

    @BeforeEach
    void setUp() {
        service = new AccountPasswordService(userRepository, passwordEncoder, jwtUtils, Clock.fixed(NOW, ZoneOffset.UTC), auditService);
        user = new User();
        user.setId(UUID.randomUUID());
        user.setPassword(passwordEncoder.encode("OldPassword1"));
        when(userRepository.findByIdForUpdate(user.getId())).thenReturn(Optional.of(user));
    }

    @Test
    void changePassword_updatesHashRevokesOldTokensAndIssuesNewOne() {
        when(jwtUtils.generateToken(any())).thenReturn("fresh-token");

        assertThat(service.changePassword(user.getId(), new ChangePasswordRequest("OldPassword1", "NewPassword1")).token())
                .isEqualTo("fresh-token");
        assertThat(passwordEncoder.matches("NewPassword1", user.getPassword())).isTrue();
        assertThat(user.getCredentialsChangedAt()).isEqualTo(NOW);
    }

    @Test
    void changePassword_rejectsWrongCurrentPassword() {
        assertThatThrownBy(() -> service.changePassword(user.getId(), new ChangePasswordRequest("Wrong1234", "NewPassword1")))
                .hasFieldOrPropertyWithValue("code", ErrorCodes.INVALID_CURRENT_PASSWORD);
        assertThat(user.getCredentialsChangedAt()).isNull();
    }

    @Test
    void changePassword_rejectsSamePassword() {
        assertThatThrownBy(() -> service.changePassword(user.getId(), new ChangePasswordRequest("OldPassword1", "OldPassword1")))
                .hasFieldOrPropertyWithValue("code", ErrorCodes.PASSWORD_REUSED);
    }
}
