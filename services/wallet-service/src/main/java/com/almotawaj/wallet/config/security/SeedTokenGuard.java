package com.almotawaj.wallet.config.security;

import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.LogMessages;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.config.constants.SeedConstants;
import com.almotawaj.wallet.exception.InformationNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Slf4j
@Component
public class SeedTokenGuard {
    private final byte[] expectedToken;

    public SeedTokenGuard(@Value(SeedConstants.SEED_TOKEN_PROPERTY) String seedToken) {
        this.expectedToken = seedToken.strip().getBytes(StandardCharsets.UTF_8);
    }

    public void verify(String authorizationHeader) {
        if (expectedToken.length == 0) {
            throw new InformationNotFoundException(ErrorMessages.NOT_FOUND);
        }
        String provided = authorizationHeader != null && authorizationHeader.startsWith(SecurityConstants.BEARER_PREFIX)
                ? authorizationHeader.substring(SecurityConstants.BEARER_PREFIX.length())
                : "";
        if (!MessageDigest.isEqual(provided.getBytes(StandardCharsets.UTF_8), expectedToken)) {
            log.warn(LogMessages.SEED_TOKEN_REJECTED);
            throw new AccessDeniedException(ErrorMessages.ACCESS_DENIED);
        }
    }
}
