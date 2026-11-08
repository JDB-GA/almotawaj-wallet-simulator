package com.almotawaj.wallet.config.security;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class WebhookSignerTest {
    private final WebhookSigner signer = new WebhookSigner("test-only-secret-that-is-long-enough");

    @Test
    void eachWebhookHasItsOwnStableSecret() {
        UUID first = UUID.randomUUID();

        assertThat(signer.secretFor(first)).startsWith("whsec_").isEqualTo(signer.secretFor(first));
        assertThat(signer.secretFor(first)).isNotEqualTo(signer.secretFor(UUID.randomUUID()));
    }

    @Test
    void signatureDependsOnTheBody() {
        UUID id = UUID.randomUUID();

        assertThat(signer.sign(id, "{\"status\":\"PAID\"}")).hasSize(64).isEqualTo(signer.sign(id, "{\"status\":\"PAID\"}"));
        assertThat(signer.sign(id, "{\"status\":\"PAID\"}")).isNotEqualTo(signer.sign(id, "{\"status\":\"REFUNDED\"}"));
    }
}
