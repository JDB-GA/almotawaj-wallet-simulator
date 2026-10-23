package com.almotawaj.wallet.controller;

import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.config.constants.SortFields;
import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.config.constants.docs.KycReviewDocs;
import com.almotawaj.wallet.config.constants.docs.UserAdminDocs;
import com.almotawaj.wallet.config.security.MyUserDetails;
import com.almotawaj.wallet.model.UserRole;
import com.almotawaj.wallet.model.UserStatus;
import com.almotawaj.wallet.model.request.CreateUserRequest;
import com.almotawaj.wallet.model.request.UpdateUserContactRequest;
import com.almotawaj.wallet.model.response.AdminUserResponse;
import com.almotawaj.wallet.model.response.AdminUserSummaryResponse;
import com.almotawaj.wallet.model.response.PageResponse;
import com.almotawaj.wallet.service.UserCreationService;
import com.almotawaj.wallet.service.UserManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Tag(name = UserAdminDocs.TAG, description = UserAdminDocs.TAG_DESCRIPTION)
@RequestMapping(ApiPaths.ADMIN_USERS)
@PreAuthorize(SecurityConstants.HAS_USER_MANAGE)
@RequiredArgsConstructor
public class AdminUserController {
    private final UserManagementService userManagementService;
    private final UserCreationService userCreationService;

    @Operation(summary = UserAdminDocs.LIST, description = UserAdminDocs.LIST_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = UserAdminDocs.LIST_OK)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = KycReviewDocs.LIST_BAD_REQUEST)
    @GetMapping
    public PageResponse<AdminUserSummaryResponse> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) UserStatus status,
            @PageableDefault(sort = SortFields.CREATED_AT, direction = Sort.Direction.DESC) Pageable pageable) {
        return userManagementService.list(search, role, status, pageable);
    }

    @Operation(summary = UserAdminDocs.CREATE, description = UserAdminDocs.CREATE_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.CREATED, description = UserAdminDocs.CREATE_CREATED)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = ApiDocs.VALIDATION_FAILED)
    @ApiResponse(responseCode = ApiDocs.CONFLICT, description = UserAdminDocs.DUPLICATE_CONTACT)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = UserAdminDocs.CREATE_UNPROCESSABLE)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdminUserResponse create(@AuthenticationPrincipal MyUserDetails actor, @Valid @RequestBody CreateUserRequest request) {
        return userCreationService.create(actor.user(), request);
    }

    @Operation(summary = UserAdminDocs.GET, description = UserAdminDocs.GET_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = UserAdminDocs.USER_OK)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = UserAdminDocs.NOT_FOUND)
    @GetMapping(ApiPaths.USER_BY_ID)
    public AdminUserResponse get(@PathVariable UUID userId) {
        return userManagementService.get(userId);
    }

    @Operation(summary = UserAdminDocs.UPDATE, description = UserAdminDocs.UPDATE_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = UserAdminDocs.USER_OK)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = ApiDocs.VALIDATION_FAILED)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = UserAdminDocs.NOT_FOUND)
    @ApiResponse(responseCode = ApiDocs.CONFLICT, description = UserAdminDocs.DUPLICATE_CONTACT)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = UserAdminDocs.STATUS_UNPROCESSABLE)
    @PatchMapping(ApiPaths.USER_BY_ID)
    public AdminUserResponse updateContact(@AuthenticationPrincipal MyUserDetails actor, @PathVariable UUID userId,
                                           @Valid @RequestBody UpdateUserContactRequest request) {
        return userManagementService.updateContact(actor.user(), userId, request);
    }

    @Operation(summary = UserAdminDocs.SUSPEND, description = UserAdminDocs.SUSPEND_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = UserAdminDocs.USER_OK)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = UserAdminDocs.NOT_FOUND)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = UserAdminDocs.STATUS_UNPROCESSABLE)
    @PostMapping(ApiPaths.USER_SUSPEND)
    public AdminUserResponse suspend(@AuthenticationPrincipal MyUserDetails actor, @PathVariable UUID userId) {
        return userManagementService.suspend(actor.user(), userId);
    }

    @Operation(summary = UserAdminDocs.REACTIVATE, description = UserAdminDocs.REACTIVATE_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = UserAdminDocs.USER_OK)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = UserAdminDocs.NOT_FOUND)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = UserAdminDocs.STATUS_UNPROCESSABLE)
    @PostMapping(ApiPaths.USER_REACTIVATE)
    public AdminUserResponse reactivate(@AuthenticationPrincipal MyUserDetails actor, @PathVariable UUID userId) {
        return userManagementService.reactivate(actor.user(), userId);
    }

    @Operation(summary = UserAdminDocs.CLOSE, description = UserAdminDocs.CLOSE_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.NO_CONTENT, description = UserAdminDocs.CLOSE_NO_CONTENT)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = UserAdminDocs.NOT_FOUND)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = UserAdminDocs.STATUS_UNPROCESSABLE)
    @DeleteMapping(ApiPaths.USER_BY_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void close(@AuthenticationPrincipal MyUserDetails actor, @PathVariable UUID userId) {
        userManagementService.close(actor.user(), userId);
    }
}
