package com.almotawaj.wallet.repository;

import com.almotawaj.wallet.model.VoucherPurchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VoucherPurchaseRepository extends JpaRepository<VoucherPurchase, UUID> {
    long countByVoucherIdAndBuyerId(UUID voucherId, UUID buyerId);

    boolean existsByCode(String code);

    @EntityGraph(attributePaths = {"voucher", "voucher.merchant"})
    Page<VoucherPurchase> findByBuyerId(UUID buyerId, Pageable pageable);
}
