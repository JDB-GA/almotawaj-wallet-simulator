package com.almotawaj.wallet.kyc.repository;

import com.almotawaj.wallet.kyc.model.KycDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KycDocumentRepository extends JpaRepository<KycDocument, UUID> {
    Optional<KycDocument> findByIdAndApplicationUserId(UUID documentId, UUID userId);

    Optional<KycDocument> findByIdAndApplicationId(UUID documentId, UUID applicationId);

    List<KycDocument> findAllByApplicationUserIdIn(Collection<UUID> userIds);

    @Modifying
    @Query("delete from KycDocument document where document.application.user.id in :userIds")
    void deleteAllByApplicationUserIdIn(Collection<UUID> userIds);
}
