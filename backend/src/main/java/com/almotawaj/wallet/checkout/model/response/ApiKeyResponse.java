package com.almotawaj.wallet.checkout.model.response;

import com.almotawaj.wallet.checkout.model.MerchantApiKey;

import java.time.Instant;
import java.util.UUID;

public record ApiKeyResponse(UUID id, String name, String keyPrefix, boolean active, Instant lastUsedAt, Instant createdAt) {
    public static ApiKeyResponse from(MerchantApiKey key) {
        return new ApiKeyResponse(key.getId(), key.getName(), key.getKeyPrefix(), key.getRevokedAt() == null,
                key.getLastUsedAt(), key.getCreatedAt());
    }
}
