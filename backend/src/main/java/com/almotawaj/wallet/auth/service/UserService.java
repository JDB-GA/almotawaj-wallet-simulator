package com.almotawaj.wallet.auth.service;

import com.almotawaj.wallet.audit.model.AuditAction;
import com.almotawaj.wallet.audit.model.AuditTargetType;
import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.auth.model.LoginResult;
import com.almotawaj.wallet.auth.model.request.LoginRequest;
import com.almotawaj.wallet.auth.model.request.RegisterRequest;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.security.JwtUtils;
import com.almotawaj.wallet.config.security.MyUserDetails;
import com.almotawaj.wallet.exception.InformationExistException;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.response.UserResponse;
import com.almotawaj.wallet.user.repository.UserRepository;
import com.almotawaj.wallet.util.LoginIdentifier;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

/**
 * Account entry points: registration, sign-in and sign-out.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final EmailVerificationService emailVerificationService;
    private final AuditService auditService;

    /**
     * Registers a new user after verifying that the email and mobile number are not already taken.
     * Saves the user with a hashed password, sends an email verification code, and returns a JWT.
     *
     * @param request the registration details supplied by the user
     * @param locale  the locale used to localise the verification email
     * @return a login result containing the JWT token and the user profile
     */
    @Transactional
    public LoginResult register(RegisterRequest request, Locale locale) {
        String email = LoginIdentifier.normalizeEmail(request.email());
        String mobileNumber = LoginIdentifier.normalizeMobile(request.mobileNumber());

        if (userRepository.existsByEmailAddress(email)) {
            throw new InformationExistException(ErrorMessages.EMAIL_ALREADY_REGISTERED, ErrorCodes.EMAIL_ALREADY_REGISTERED);
        }
        if (userRepository.existsByMobileNumber(mobileNumber)) {
            throw new InformationExistException(ErrorMessages.MOBILE_ALREADY_REGISTERED, ErrorCodes.MOBILE_ALREADY_REGISTERED);
        }

        User user = new User();
        user.setEmailAddress(email);
        user.setMobileNumber(mobileNumber);
        user.setPassword(passwordEncoder.encode(request.password()));

        User saved = userRepository.save(user);
        emailVerificationService.sendCode(saved, locale);
        auditService.record(saved.getId(), AuditAction.USER_REGISTERED, AuditTargetType.USER, saved.getId(), null);

        return new LoginResult(jwtUtils.generateToken(new MyUserDetails(saved)), UserResponse.from(saved));
    }

    /**
     * Authenticates a user by their email or mobile number and password.
     * Sends a verification code if none is currently active for the account.
     *
     * @param request the login credentials supplied by the user
     * @param locale  the locale used to localise any verification email
     * @return a login result containing the JWT token and the user profile
     */
    public LoginResult login(LoginRequest request, Locale locale) {
        String identifier = LoginIdentifier.normalize(request.identifier());
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(identifier, request.password()));

        if (!(authentication.getPrincipal() instanceof MyUserDetails userDetails)) {
            throw new IllegalStateException(ErrorMessages.UNEXPECTED_PRINCIPAL);
        }

        User user = userDetails.user();
        emailVerificationService.sendCodeIfNoneActive(user, locale);
        auditService.record(user.getId(), AuditAction.USER_LOGGED_IN, AuditTargetType.USER, user.getId(), null);

        return new LoginResult(jwtUtils.generateToken(userDetails), UserResponse.from(user));
    }

    /**
     * Records that a signed-in user ended their session. The cookie itself is cleared by the controller.
     *
     * @param userId the id of the user who is signing out
     */
    public void logout(UUID userId) {
        auditService.record(userId, AuditAction.USER_LOGGED_OUT, AuditTargetType.USER, userId, null);
    }
}
