package com.almotawaj.wallet.service;

import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WebhookUrlPolicyTest {
    private final WebhookUrlPolicy policy = new WebhookUrlPolicy(false);

    @Test
    void acceptsAPublicHttpsAddress() {
        assertThat(policy.requireAllowed("https://8.8.8.8/webhooks/wallet")).hasHost("8.8.8.8");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "http://8.8.8.8/hook",
            "https://127.0.0.1/hook",
            "https://10.0.0.5/hook",
            "https://192.168.1.10/hook",
            "https://169.254.169.254/latest/meta-data",
            "https://100.64.0.1/hook",
            "https://[::1]/hook",
            "https://[fd00::1]/hook",
            "https://user:secret@8.8.8.8/hook",
            "https:///hook"
    })
    void rejectsInsecureOrInternalAddresses(String url) {
        assertThatThrownBy(() -> policy.requireAllowed(url))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("code", ErrorCodes.WEBHOOK_URL_NOT_ALLOWED);
    }

    @Test
    void localAddressesAreOnlyAllowedWhenEnabledForDevelopment() {
        assertThat(new WebhookUrlPolicy(true).requireAllowed("http://localhost:3000/hook")).hasPort(3000);
    }
}
