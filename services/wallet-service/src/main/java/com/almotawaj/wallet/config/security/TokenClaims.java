package com.almotawaj.wallet.config.security;

import java.time.Instant;

public record TokenClaims(String username, Instant issuedAt) {
}
