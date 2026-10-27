package com.almotawaj.wallet.auth.repository;

import com.almotawaj.wallet.auth.model.OtpChallenge;
import com.almotawaj.wallet.auth.model.OtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface OtpChallengeRepository extends JpaRepository<OtpChallenge, UUID> {
    Optional<OtpChallenge> findFirstByUser_IdAndPurposeOrderByCreatedAtDesc(UUID userId, OtpPurpose purpose);

    void deleteByUser_IdAndPurpose(UUID userId, OtpPurpose purpose);

    @Modifying
    @Query("delete from OtpChallenge challenge where challenge.user.id in :userIds")
    void deleteAllByUserIdIn(Collection<UUID> userIds);
}
