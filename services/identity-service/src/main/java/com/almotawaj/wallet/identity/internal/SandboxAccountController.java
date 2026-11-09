package com.almotawaj.wallet.identity.internal;

import com.almotawaj.wallet.identity.config.constants.InternalConstants;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Hidden
@RestController
@RequestMapping(InternalConstants.SANDBOX_ACCOUNTS_PATH)
@RequiredArgsConstructor
public class SandboxAccountController {
    private final SandboxAccountService sandboxAccountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<UUID> create(@RequestBody SandboxAccountRequest request) {
        return sandboxAccountService.create(request);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestBody List<UUID> userIds) {
        sandboxAccountService.delete(userIds);
    }
}
