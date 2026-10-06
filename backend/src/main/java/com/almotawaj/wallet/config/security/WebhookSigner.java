package com.almotawaj.wallet.config.security;

import com.almotawaj.wallet.config.constants.GatewayConstants;
import com.almotawaj.wallet.config.constants.OtpConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HexFormat;
import java.util.UUID;

@Component
public class WebhookSigner {
    private final byte[] serverSecret;

    public WebhookSigner(@Value(OtpConstants.SECRET_PROPERTY) String serverSecret) {
        this.serverSecret = serverSecret.getBytes(StandardCharsets.UTF_8);
    }

    public String secretFor(UUID webhookId) {
        return GatewayConstants.WEBHOOK_SECRET_PREFIX + hmac(serverSecret, GatewayConstants.WEBHOOK_SECRET_LABEL + webhookId);
    }

    public String sign(UUID webhookId, String body) {
        return hmac(secretFor(webhookId).getBytes(StandardCharsets.UTF_8), body);
    }

    private static String hmac(byte[] key, String message) {
        try {
            Mac mac = Mac.getInstance(OtpConstants.HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(key, OtpConstants.HMAC_ALGORITHM));
            return HexFormat.of().formatHex(mac.doFinal(message.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }
}
