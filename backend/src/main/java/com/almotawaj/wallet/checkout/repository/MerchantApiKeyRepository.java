package com.almotawaj.wallet.checkout.repository;

import com.almotawaj.wallet.checkout.model.MerchantApiKey;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MerchantApiKeyRepository extends JpaRepository<MerchantApiKey, UUID> {
    List<MerchantApiKey> findAllByMerchantIdOrderByCreatedAtDesc(UUID merchantId);

    Optional<MerchantApiKey> findByIdAndMerchantId(UUID id, UUID merchantId);

    long countByMerchantIdAndRevokedAtIsNull(UUID merchantId);

    @EntityGraph(attributePaths = "merchant")
    Optional<MerchantApiKey> findByKeyHashAndRevokedAtIsNull(String keyHash);

    @Modifying
    @Query("delete from MerchantApiKey k where k.merchant.id in :userIds")
    void deleteAllByMerchantIdIn(Collection<UUID> userIds);
}
