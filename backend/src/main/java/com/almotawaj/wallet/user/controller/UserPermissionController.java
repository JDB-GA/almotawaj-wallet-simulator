package com.almotawaj.wallet.user.controller;

import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.config.constants.docs.KycReviewDocs;
import com.almotawaj.wallet.config.constants.docs.UserAdminDocs;
import com.almotawaj.wallet.config.security.MyUserDetails;
import com.almotawaj.wallet.user.model.Permission;
import com.almotawaj.wallet.user.model.response.AdminUserResponse;
import com.almotawaj.wallet.user.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Tag(name = UserAdminDocs.PERMISSIONS_TAG, description = UserAdminDocs.PERMISSIONS_TAG_DESCRIPTION)
@RequestMapping(ApiPaths.ADMIN_USERS)
@PreAuthorize(SecurityConstants.HAS_ROLE_SUPER_ADMIN)
@RequiredArgsConstructor
public class UserPermissionController {
    private final PermissionService permissionService;

    @Operation(summary = UserAdminDocs.GRANT, description = UserAdminDocs.GRANT_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = UserAdminDocs.USER_OK)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = KycReviewDocs.LIST_BAD_REQUEST)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = UserAdminDocs.NOT_FOUND)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = UserAdminDocs.PERMISSION_UNPROCESSABLE)
    @PutMapping(ApiPaths.USER_PERMISSION)
    public AdminUserResponse grant(@AuthenticationPrincipal MyUserDetails actor, @PathVariable UUID userId,
                                   @PathVariable Permission permission) {
        return permissionService.grant(actor.user().getId(), userId, permission);
    }

    @Operation(summary = UserAdminDocs.REVOKE, description = UserAdminDocs.REVOKE_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = UserAdminDocs.USER_OK)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = UserAdminDocs.NOT_FOUND)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = UserAdminDocs.PERMISSION_UNPROCESSABLE)
    @DeleteMapping(ApiPaths.USER_PERMISSION)
    public AdminUserResponse revoke(@AuthenticationPrincipal MyUserDetails actor, @PathVariable UUID userId,
                                    @PathVariable Permission permission) {
        return permissionService.revoke(actor.user().getId(), userId, permission);
    }
}
