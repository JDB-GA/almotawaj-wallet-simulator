package com.almotawaj.wallet.seed.controller;

import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.seed.service.SeedDataCleanupService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Hidden
@RestController
@RequestMapping(ApiPaths.INTERNAL_SEED_DATA)
@RequiredArgsConstructor
public class SeedDataCleanupController {
    private final SeedDataCleanupService seedDataCleanupService;

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clean(@RequestBody List<UUID> userIds) {
        seedDataCleanupService.clean(userIds);
    }
}
