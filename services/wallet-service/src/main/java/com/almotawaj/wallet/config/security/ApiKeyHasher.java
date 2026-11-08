package com.almotawaj.wallet.config.security;

import com.almotawaj.wallet.config.constants.GatewayConstants;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

@Component
public class ApiKeyHasher {
    private final SecureRandom random = new SecureRandom();

    public String generate() {
        byte[] bytes = new byte[GatewayConstants.API_KEY_RANDOM_BYTES];
        random.nextBytes(bytes);
        return GatewayConstants.API_KEY_PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String hash(String apiKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance(GatewayConstants.API_KEY_HASH_ALGORITHM);
            return HexFormat.of().formatHex(digest.digest(apiKey.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
