package com.almotawaj.wallet.checkout.service;

import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.checkout.model.MerchantApiKey;
import com.almotawaj.wallet.checkout.model.request.CreateApiKeyRequest;
import com.almotawaj.wallet.checkout.repository.MerchantApiKeyRepository;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.GatewayConstants;
import com.almotawaj.wallet.config.security.ApiKeyHasher;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.UserRole;
import com.almotawaj.wallet.user.model.UserStatus;
import com.almotawaj.wallet.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MerchantApiKeyServiceTest {
    private static final Instant NOW = Instant.parse("2026-10-06T10:00:00Z");

    @Mock
    private MerchantApiKeyRepository apiKeyRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuditService auditService;

    private final ApiKeyHasher hasher = new ApiKeyHasher();
    private MerchantApiKeyService service;
    private User merchant;

    @BeforeEach
    void setUp() {
        service = new MerchantApiKeyService(apiKeyRepository, userRepository, hasher, auditService, Clock.fixed(NOW, ZoneOffset.UTC));
        merchant = new User();
        merchant.setId(UUID.randomUUID());
        merchant.setRole(UserRole.MERCHANT);
        merchant.setEmailVerified(true);
        when(userRepository.findByIdForUpdate(merchant.getId())).thenReturn(Optional.of(merchant));
        when(apiKeyRepository.saveAndFlush(any(MerchantApiKey.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void create_returnsTheSecretOnceAndStoresOnlyItsHash() {
        var created = service.create(merchant.getId(), new CreateApiKeyRequest(" Online store "));

        assertThat(created.secret()).startsWith(GatewayConstants.API_KEY_PREFIX);
        assertThat(created.key().name()).isEqualTo("Online store");
        assertThat(created.secret()).startsWith(created.key().keyPrefix());
        assertThat(created.key().active()).isTrue();
    }

    @Test
    void create_rejectsMoreThanTheAllowedActiveKeys() {
        when(apiKeyRepository.countByMerchantIdAndRevokedAtIsNull(merchant.getId()))
                .thenReturn((long) GatewayConstants.MAX_ACTIVE_API_KEYS);

        assertThatThrownBy(() -> service.create(merchant.getId(), new CreateApiKeyRequest("Sixth")))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("code", ErrorCodes.API_KEY_LIMIT_REACHED);
    }

    @Test
    void authenticate_acceptsAnActiveKeyAndRecordsItsUse() {
        MerchantApiKey key = storedKey("almt_secret");

        assertThat(service.authenticate("almt_secret")).containsSame(merchant);
        assertThat(key.getLastUsedAt()).isEqualTo(NOW);
        assertThat(service.authenticate("almt_other")).isEmpty();
    }

    @Test
    void authenticate_rejectsKeysOfSuspendedMerchants() {
        storedKey("almt_secret");
        merchant.setStatus(UserStatus.SUSPENDED);

        assertThat(service.authenticate("almt_secret")).isEmpty();
    }

    private MerchantApiKey storedKey(String secret) {
        MerchantApiKey key = new MerchantApiKey();
        key.setMerchant(merchant);
        when(apiKeyRepository.findByKeyHashAndRevokedAtIsNull(hasher.hash(secret))).thenReturn(Optional.of(key));
        return key;
    }
}
