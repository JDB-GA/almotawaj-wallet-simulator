package com.almotawaj.wallet.identity.config.security;

import java.time.Instant;

public record TokenClaims(String username, Instant issuedAt) {
}
