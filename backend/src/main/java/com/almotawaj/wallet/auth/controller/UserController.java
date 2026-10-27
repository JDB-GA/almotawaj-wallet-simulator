package com.almotawaj.wallet.auth.controller;

import com.almotawaj.wallet.auth.model.LoginResult;
import com.almotawaj.wallet.auth.model.request.LoginRequest;
import com.almotawaj.wallet.auth.model.request.RegisterRequest;
import com.almotawaj.wallet.auth.service.UserService;
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
public class UserController {
    private final UserService userService;
    private final AuthCookieFactory authCookieFactory;

    @Operation(summary = AuthDocs.REGISTER, description = AuthDocs.REGISTER_DESCRIPTION)
    @SecurityRequirements
    @ApiResponse(responseCode = ApiDocs.CREATED, description = AuthDocs.REGISTER_CREATED)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = ApiDocs.VALIDATION_FAILED)
    @ApiResponse(responseCode = ApiDocs.CONFLICT, description = AuthDocs.REGISTER_CONFLICT)
    @ApiResponse(responseCode = ApiDocs.TOO_MANY_REQUESTS, description = ApiDocs.RATE_LIMITED)
    @PostMapping(ApiPaths.REGISTER)
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request, Locale locale) {
        return withAuthCookie(ResponseEntity.status(HttpStatus.CREATED), userService.register(request, locale));
    }

    @Operation(summary = AuthDocs.LOGIN, description = AuthDocs.LOGIN_DESCRIPTION)
    @SecurityRequirements
    @ApiResponse(responseCode = ApiDocs.OK, description = AuthDocs.LOGIN_OK)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = ApiDocs.VALIDATION_FAILED)
    @ApiResponse(responseCode = ApiDocs.UNAUTHORIZED, description = AuthDocs.LOGIN_UNAUTHORIZED)
    @ApiResponse(responseCode = ApiDocs.TOO_MANY_REQUESTS, description = ApiDocs.RATE_LIMITED)
    @PostMapping(ApiPaths.LOGIN)
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request, Locale locale) {
        return withAuthCookie(ResponseEntity.ok(), userService.login(request, locale));
    }

    @Operation(summary = AuthDocs.LOGOUT, description = AuthDocs.LOGOUT_DESCRIPTION)
    @SecurityRequirements
    @ApiResponse(responseCode = ApiDocs.NO_CONTENT, description = AuthDocs.LOGOUT_NO_CONTENT)
    @PostMapping(ApiPaths.LOGOUT)
    public ResponseEntity<Void> logout(@AuthenticationPrincipal MyUserDetails userDetails) {
        if (userDetails != null) {
            userService.logout(userDetails.user().getId());
        }
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, authCookieFactory.clear().toString())
                .build();
    }

    @Operation(summary = AuthDocs.ME, description = AuthDocs.ME_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = AuthDocs.ME_OK)
    @GetMapping(ApiPaths.ME)
    public UserResponse me(@AuthenticationPrincipal MyUserDetails userDetails) {
        return UserResponse.from(userDetails.user());
    }

    private ResponseEntity<UserResponse> withAuthCookie(ResponseEntity.BodyBuilder builder, LoginResult result) {
        return builder
                .header(HttpHeaders.SET_COOKIE, authCookieFactory.create(result.token()).toString())
                .body(result.user());
    }
}
