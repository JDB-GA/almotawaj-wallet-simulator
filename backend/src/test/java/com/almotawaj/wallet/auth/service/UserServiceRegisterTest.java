package com.almotawaj.wallet.auth.service;

import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.auth.model.LoginResult;
import com.almotawaj.wallet.auth.model.request.RegisterRequest;
import com.almotawaj.wallet.config.security.JwtUtils;
import com.almotawaj.wallet.exception.InformationExistException;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.UserRole;
import com.almotawaj.wallet.user.model.UserStatus;
import com.almotawaj.wallet.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceRegisterTest {
    private static final String LOCAL_MOBILE = "33123456";
    private static final String MOBILE = "+97333123456";
    private static final Locale LOCALE = Locale.ENGLISH;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private EmailVerificationService emailVerificationService;
    @Mock
    private AuditService auditService;

    @InjectMocks
    private UserService userService;

    @Test
    void register_savesUserSendsCodeAndReturnsToken() {
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtUtils.generateToken(any())).thenReturn("jwt-token");

        LoginResult result = userService.register(new RegisterRequest("  Test@Mail.COM ", " " + LOCAL_MOBILE + " ", "password123"), LOCALE);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertThat(saved.getEmailAddress()).isEqualTo("test@mail.com");
        assertThat(saved.getMobileNumber()).isEqualTo(MOBILE);
        assertThat(saved.getPassword()).isEqualTo("hashed");
        assertThat(saved.getRole()).isEqualTo(UserRole.CLIENT);
        assertThat(saved.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(saved.isEmailVerified()).isFalse();
        assertThat(saved.isMobileVerified()).isTrue();
        assertThat(result.token()).isEqualTo("jwt-token");
        verify(emailVerificationService).sendCode(saved, LOCALE);
    }

    @Test
    void register_rejectsDuplicateEmail() {
        when(userRepository.existsByEmailAddress("test@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(new RegisterRequest("test@mail.com", LOCAL_MOBILE, "password123"), LOCALE))
                .isInstanceOf(InformationExistException.class);

        verify(userRepository, never()).save(any());
        verifyNoInteractions(emailVerificationService, auditService);
    }

    @Test
    void register_rejectsDuplicateMobile() {
        when(userRepository.existsByMobileNumber(MOBILE)).thenReturn(true);

        assertThatThrownBy(() -> userService.register(new RegisterRequest("new@mail.com", LOCAL_MOBILE, "password123"), LOCALE))
                .isInstanceOf(InformationExistException.class);

        verify(userRepository, never()).save(any());
        verifyNoInteractions(emailVerificationService, auditService);
    }
}
