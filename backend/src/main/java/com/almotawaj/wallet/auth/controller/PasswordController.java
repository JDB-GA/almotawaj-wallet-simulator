package com.almotawaj.wallet.auth.controller;

import com.almotawaj.wallet.auth.model.LoginResult;
import com.almotawaj.wallet.auth.model.request.ChangePasswordRequest;
import com.almotawaj.wallet.auth.model.request.ForgotPasswordRequest;
import com.almotawaj.wallet.auth.model.request.ResetPasswordRequest;
import com.almotawaj.wallet.auth.service.AccountPasswordService;
import com.almotawaj.wallet.auth.service.PasswordResetService;
import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.config.constants.docs.AuthDocs;
import com.almotawaj.wallet.config.security.AuthCookieFactory;
import com.almotawaj.wallet.config.security.MyUserDetails;
import com.almotawaj.wallet.user.model.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@Tag(name = AuthDocs.TAG, description = AuthDocs.TAG_DESCRIPTION)
@RequestMapping(ApiPaths.AUTH_USERS)
@RequiredArgsConstructor
public class PasswordController {
    private final PasswordResetService passwordResetService;
    private final AccountPasswordService accountPasswordService;
    private final AuthCookieFactory authCookieFactory;

    @Operation(summary = AuthDocs.FORGOT, description = AuthDocs.FORGOT_DESCRIPTION)
    @SecurityRequirements
    @ApiResponse(responseCode = ApiDocs.NO_CONTENT, description = AuthDocs.FORGOT_NO_CONTENT)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = ApiDocs.VALIDATION_FAILED)
    @ApiResponse(responseCode = ApiDocs.TOO_MANY_REQUESTS, description = ApiDocs.RATE_LIMITED)
    @PostMapping(ApiPaths.FORGOT_PASSWORD)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void forgotPassword(@Valid @RequestBody ForgotPasswordRequest request, Locale locale) {
        passwordResetService.requestReset(request.email(), locale);
    }

    @Operation(summary = AuthDocs.RESET, description = AuthDocs.RESET_DESCRIPTION)
    @SecurityRequirements
    @ApiResponse(responseCode = ApiDocs.NO_CONTENT, description = AuthDocs.RESET_NO_CONTENT)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = AuthDocs.RESET_BAD_REQUEST)
    @ApiResponse(responseCode = ApiDocs.TOO_MANY_REQUESTS, description = ApiDocs.RATE_LIMITED)
    @PostMapping(ApiPaths.RESET_PASSWORD)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.reset(request);
    }

    @Operation(summary = AuthDocs.CHANGE, description = AuthDocs.CHANGE_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = AuthDocs.CHANGE_OK)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = ApiDocs.VALIDATION_FAILED)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = AuthDocs.CHANGE_UNPROCESSABLE)
    @ApiResponse(responseCode = ApiDocs.TOO_MANY_REQUESTS, description = ApiDocs.RATE_LIMITED)
    @PostMapping(ApiPaths.CHANGE_PASSWORD)
    public ResponseEntity<UserResponse> changePassword(@AuthenticationPrincipal MyUserDetails userDetails,
                                                       @Valid @RequestBody ChangePasswordRequest request) {
        LoginResult result = accountPasswordService.changePassword(userDetails.user().getId(), request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authCookieFactory.create(result.token()).toString())
                .body(result.user());
    }
}
