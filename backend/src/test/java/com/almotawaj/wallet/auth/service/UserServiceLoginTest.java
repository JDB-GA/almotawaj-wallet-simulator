package com.almotawaj.wallet.auth.service;

import com.almotawaj.wallet.audit.model.AuditAction;
import com.almotawaj.wallet.audit.model.AuditTargetType;
import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.auth.model.request.LoginRequest;
import com.almotawaj.wallet.config.security.JwtUtils;
import com.almotawaj.wallet.config.security.MyUserDetails;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceLoginTest {
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
    void login_withEmail_normalizesReturnsTokenAndSendsCodeIfNeeded() {
        User user = new User();
        user.setId(UUID.randomUUID());
        MyUserDetails userDetails = new MyUserDetails(user);
        Authentication authenticated =
                UsernamePasswordAuthenticationToken.authenticated(userDetails, null, userDetails.getAuthorities());
        when(authenticationManager.authenticate(any())).thenReturn(authenticated);
        when(jwtUtils.generateToken(userDetails)).thenReturn("jwt-token");

        assertThat(userService.login(new LoginRequest(" Test@Mail.com", "password123"), LOCALE).token()).isEqualTo("jwt-token");

        ArgumentCaptor<Authentication> captor = ArgumentCaptor.forClass(Authentication.class);
        verify(authenticationManager).authenticate(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("test@mail.com");
        verify(emailVerificationService).sendCodeIfNoneActive(user, LOCALE);
        verify(auditService).record(user.getId(), AuditAction.USER_LOGGED_IN, AuditTargetType.USER, user.getId(), null);
    }

    @Test
    void login_withMobile_addsCountryCodeAndSendsNothingOnFailure() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));

        assertThatThrownBy(() -> userService.login(new LoginRequest(" 33123456 ", "wrong"), LOCALE))
                .isInstanceOf(BadCredentialsException.class);

        ArgumentCaptor<Authentication> captor = ArgumentCaptor.forClass(Authentication.class);
        verify(authenticationManager).authenticate(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("+97333123456");
        verifyNoInteractions(jwtUtils, emailVerificationService, auditService);
    }
}
