package com.almotawaj.wallet.config.security;

import java.util.List;
import java.util.UUID;

public record TokenClaims(UUID userId, String email, List<String> authorities) {
}
