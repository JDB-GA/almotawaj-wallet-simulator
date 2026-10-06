package com.almotawaj.wallet.service;

import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.GatewayMessages;
import com.almotawaj.wallet.config.constants.GatewayConstants;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import com.almotawaj.wallet.config.security.ApiKeyHasher;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.exception.InformationNotFoundException;
import com.almotawaj.wallet.model.AuditAction;
import com.almotawaj.wallet.model.AuditTargetType;
import com.almotawaj.wallet.model.MerchantApiKey;
import com.almotawaj.wallet.model.User;
import com.almotawaj.wallet.model.UserRole;
import com.almotawaj.wallet.model.UserStatus;
import com.almotawaj.wallet.model.request.CreateApiKeyRequest;
import com.almotawaj.wallet.model.response.ApiKeyCreatedResponse;
import com.almotawaj.wallet.model.response.ApiKeyResponse;
import com.almotawaj.wallet.repository.MerchantApiKeyRepository;
import com.almotawaj.wallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MerchantApiKeyService {
    private final MerchantApiKeyRepository apiKeyRepository;
    private final UserRepository userRepository;
    private final ApiKeyHasher hasher;
    private final AuditService auditService;
    private final Clock clock;

    @Transactional(readOnly = true)
    public List<ApiKeyResponse> list(UUID merchantId) {
        return apiKeyRepository.findAllByMerchantIdOrderByCreatedAtDesc(merchantId).stream().map(ApiKeyResponse::from).toList();
    }

    @Transactional
    public ApiKeyCreatedResponse create(UUID merchantId, CreateApiKeyRequest request) {
        userRepository.findByIdForUpdate(merchantId)
                .orElseThrow(() -> new InformationNotFoundException(ErrorMessages.USER_NOT_FOUND));
        if (apiKeyRepository.countByMerchantIdAndRevokedAtIsNull(merchantId) >= GatewayConstants.MAX_ACTIVE_API_KEYS) {
            throw new BusinessRuleException(GatewayMessages.API_KEY_LIMIT_REACHED, ErrorCodes.API_KEY_LIMIT_REACHED);
        }
        String secret = hasher.generate();
        MerchantApiKey key = new MerchantApiKey();
        key.setMerchant(userRepository.getReferenceById(merchantId));
        key.setName(request.name().strip());
        key.setKeyPrefix(secret.substring(0, ValidationLimits.API_KEY_PREFIX_LENGTH));
        key.setKeyHash(hasher.hash(secret));
        MerchantApiKey saved = apiKeyRepository.saveAndFlush(key);
        auditService.record(merchantId, AuditAction.API_KEY_CREATED, AuditTargetType.API_KEY, saved.getId(), saved.getName());
        return new ApiKeyCreatedResponse(ApiKeyResponse.from(saved), secret);
    }

    @Transactional
    public void revoke(UUID merchantId, UUID keyId) {
        MerchantApiKey key = apiKeyRepository.findByIdAndMerchantId(keyId, merchantId)
                .orElseThrow(() -> new InformationNotFoundException(GatewayMessages.API_KEY_NOT_FOUND, ErrorCodes.API_KEY_NOT_FOUND));
        if (key.getRevokedAt() == null) {
            key.setRevokedAt(clock.instant());
            auditService.record(merchantId, AuditAction.API_KEY_REVOKED, AuditTargetType.API_KEY, keyId, key.getName());
        }
    }

    @Transactional
    public Optional<User> authenticate(String apiKey) {
        return apiKeyRepository.findByKeyHashAndRevokedAtIsNull(hasher.hash(apiKey))
                .filter(key -> isActiveMerchant(key.getMerchant()))
                .map(key -> {
                    key.setLastUsedAt(clock.instant());
                    return key.getMerchant();
                });
    }

    private static boolean isActiveMerchant(User user) {
        return user.getRole() == UserRole.MERCHANT && user.getStatus() == UserStatus.ACTIVE && user.isEmailVerified();
    }
}
