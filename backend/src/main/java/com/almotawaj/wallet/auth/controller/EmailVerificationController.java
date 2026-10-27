package com.almotawaj.wallet.auth.controller;

import com.almotawaj.wallet.auth.model.request.VerifyEmailRequest;
import com.almotawaj.wallet.auth.service.EmailVerificationService;
import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.config.constants.docs.AuthDocs;
import com.almotawaj.wallet.config.security.MyUserDetails;
import com.almotawaj.wallet.user.model.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@Tag(name = AuthDocs.TAG, description = AuthDocs.TAG_DESCRIPTION)
@RequestMapping(ApiPaths.AUTH_USERS)
@RequiredArgsConstructor
public class EmailVerificationController {
    private final EmailVerificationService emailVerificationService;

    @Operation(summary = AuthDocs.VERIFY_EMAIL, description = AuthDocs.VERIFY_EMAIL_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = AuthDocs.VERIFY_EMAIL_OK)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = AuthDocs.VERIFY_EMAIL_BAD_REQUEST)
    @ApiResponse(responseCode = ApiDocs.CONFLICT, description = AuthDocs.ALREADY_VERIFIED)
    @ApiResponse(responseCode = ApiDocs.TOO_MANY_REQUESTS, description = ApiDocs.RATE_LIMITED)
    @PostMapping(ApiPaths.VERIFY_EMAIL)
    public UserResponse verifyEmail(@AuthenticationPrincipal MyUserDetails userDetails,
                                    @Valid @RequestBody VerifyEmailRequest request) {
        return emailVerificationService.verify(userDetails.user().getId(), request.code());
    }

    @Operation(summary = AuthDocs.RESEND, description = AuthDocs.RESEND_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.NO_CONTENT, description = AuthDocs.RESEND_NO_CONTENT)
    @ApiResponse(responseCode = ApiDocs.CONFLICT, description = AuthDocs.ALREADY_VERIFIED)
    @ApiResponse(responseCode = ApiDocs.TOO_MANY_REQUESTS, description = ApiDocs.RATE_LIMITED)
    @PostMapping(ApiPaths.RESEND_VERIFICATION)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resendVerification(@AuthenticationPrincipal MyUserDetails userDetails, Locale locale) {
        emailVerificationService.resend(userDetails.user().getId(), locale);
    }
}
