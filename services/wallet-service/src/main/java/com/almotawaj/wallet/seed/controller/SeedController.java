package com.almotawaj.wallet.seed.controller;

import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.config.constants.docs.SystemDocs;
import com.almotawaj.wallet.config.security.SeedTokenGuard;
import com.almotawaj.wallet.seed.service.DatabaseSeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = SystemDocs.SEED_TAG, description = SystemDocs.SEED_TAG_DESCRIPTION)
@RequestMapping(ApiPaths.SEED)
@RequiredArgsConstructor
public class SeedController {
    private final DatabaseSeedService seedService;
    private final SeedTokenGuard seedTokenGuard;

    @Operation(summary = SystemDocs.SEED, description = SystemDocs.SEED_DESCRIPTION)
    @SecurityRequirement(name = ApiDocs.SEED_SCHEME_NAME)
    @ApiResponse(responseCode = ApiDocs.CREATED, description = SystemDocs.SEED_CREATED)
    @ApiResponse(responseCode = ApiDocs.FORBIDDEN, description = SystemDocs.SEED_FORBIDDEN)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = SystemDocs.SEED_NOT_FOUND)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = SystemDocs.SEED_UNPROCESSABLE)
    @ApiResponse(responseCode = ApiDocs.TOO_MANY_REQUESTS, description = ApiDocs.RATE_LIMITED)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void seedData(@Parameter(hidden = true) @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        seedTokenGuard.verify(authorization);
        seedService.seedBasicData();
    }
}
