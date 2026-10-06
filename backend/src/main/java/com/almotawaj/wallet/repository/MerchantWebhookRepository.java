package com.almotawaj.wallet.repository;

import com.almotawaj.wallet.model.MerchantWebhook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface MerchantWebhookRepository extends JpaRepository<MerchantWebhook, UUID> {
    Optional<MerchantWebhook> findByMerchantId(UUID merchantId);

    @Modifying
    @Query("delete from MerchantWebhook w where w.merchant.id in :userIds")
    void deleteAllByMerchantIdIn(Collection<UUID> userIds);
}
