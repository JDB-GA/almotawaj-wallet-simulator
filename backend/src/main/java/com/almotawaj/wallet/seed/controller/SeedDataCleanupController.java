package com.almotawaj.wallet.seed.controller;

import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.config.constants.docs.SystemDocs;
import com.almotawaj.wallet.seed.service.SeedDataCleanupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = SystemDocs.SEED_TAG, description = SystemDocs.SEED_TAG_DESCRIPTION)
@RequestMapping(ApiPaths.ADMIN_SEED_DATA)
@PreAuthorize(SecurityConstants.HAS_ROLE_SUPER_ADMIN)
@RequiredArgsConstructor
public class SeedDataCleanupController {
    private final SeedDataCleanupService seedDataCleanupService;

    @Operation(summary = SystemDocs.CLEAN_SEED_DATA, description = SystemDocs.CLEAN_SEED_DATA_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.NO_CONTENT, description = SystemDocs.CLEAN_SEED_DATA_NO_CONTENT)
    @ApiResponse(responseCode = ApiDocs.FORBIDDEN, description = ApiDocs.FORBIDDEN)
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clean() {
        seedDataCleanupService.clean();
    }
}
