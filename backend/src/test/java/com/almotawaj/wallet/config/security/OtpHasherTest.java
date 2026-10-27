package com.almotawaj.wallet.config.security;

import com.almotawaj.wallet.auth.model.OtpPurpose;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OtpHasherTest {
    private static final OtpPurpose PURPOSE = OtpPurpose.EMAIL_VERIFICATION;
    private static final UUID USER_ID = UUID.randomUUID();

    private final OtpHasher otpHasher = new OtpHasher("test-otp-secret-that-is-long-enough-123456");

    @Test
    void hash_isDeterministicAndNotThePlainCode() {
        String hash = otpHasher.hash(USER_ID, PURPOSE, "123456");

        assertThat(hash).isEqualTo(otpHasher.hash(USER_ID, PURPOSE, "123456")).hasSize(64).doesNotContain("123456");
    }

    @Test
    void matches_onlyForSameCodeAndUser() {
        String hash = otpHasher.hash(USER_ID, PURPOSE, "123456");

        assertThat(otpHasher.matches(USER_ID, PURPOSE, "123456", hash)).isTrue();
        assertThat(otpHasher.matches(USER_ID, PURPOSE, "654321", hash)).isFalse();
        assertThat(otpHasher.matches(UUID.randomUUID(), PURPOSE, "123456", hash)).isFalse();
    }
}
