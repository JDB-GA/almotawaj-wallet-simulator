package com.almotawaj.wallet.config.security;

import java.util.List;
import java.util.UUID;

/** What wallet reads from a token issued by identity-service. */
public record TokenClaims(UUID userId, String email, List<String> authorities) {
}
