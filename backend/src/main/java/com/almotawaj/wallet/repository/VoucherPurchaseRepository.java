package com.almotawaj.wallet.repository;

import com.almotawaj.wallet.model.CheckoutSession;
import com.almotawaj.wallet.model.VoucherPurchase;
import com.almotawaj.wallet.model.VoucherPurchaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface VoucherPurchaseRepository extends JpaRepository<VoucherPurchase, UUID> {
    long countByVoucherIdAndBuyerId(UUID voucherId, UUID buyerId);

    boolean existsByCode(String code);

    @EntityGraph(attributePaths = {"voucher", "voucher.merchant"})
    Page<VoucherPurchase> findByBuyerId(UUID buyerId, Pageable pageable);

    @Query("""
            select p from VoucherPurchase p join fetch p.voucher v
            where p.buyer.id = :buyerId and v.merchant.id = :merchantId and p.status = :status
            and (v.expiresAt is null or v.expiresAt > :now)
            order by p.createdAt""")
    List<VoucherPurchase> findUsable(UUID buyerId, UUID merchantId, VoucherPurchaseStatus status, Instant now);

    @Modifying(flushAutomatically = true)
    @Query("""
            update VoucherPurchase p
            set p.status = :redeemed, p.redeemedCheckout = :checkout, p.redeemedAt = :now
            where p.id = :id and p.buyer.id = :buyerId and p.status = :unused
            and p.voucher.id in (select v.id from Voucher v where v.merchant.id = :merchantId
                and (v.expiresAt is null or v.expiresAt > :now))""")
    int redeem(UUID id, UUID buyerId, UUID merchantId, CheckoutSession checkout, Instant now,
               VoucherPurchaseStatus unused, VoucherPurchaseStatus redeemed);

    @Modifying(flushAutomatically = true)
    @Query("""
            update VoucherPurchase p
            set p.status = :unused, p.redeemedCheckout = null, p.redeemedAt = null
            where p.redeemedCheckout.id = :checkoutId""")
    int release(UUID checkoutId, VoucherPurchaseStatus unused);

    @Modifying
    @Query("""
            delete from VoucherPurchase p
            where p.buyer.id in :userIds or p.voucher.id in (select v.id from Voucher v where v.merchant.id in :userIds)""")
    void deleteAllByUserIdIn(Collection<UUID> userIds);
}
