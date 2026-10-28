package com.almotawaj.wallet.voucher.repository;

import com.almotawaj.wallet.voucher.model.Voucher;
import com.almotawaj.wallet.voucher.model.VoucherStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface VoucherRepository extends JpaRepository<Voucher, UUID> {
    Page<Voucher> findByMerchantId(UUID merchantId, Pageable pageable);

    Optional<Voucher> findByIdAndMerchantId(UUID id, UUID merchantId);

    boolean existsByMerchantIdAndTitle(UUID merchantId, String title);

    @Query(value = """
            select v from Voucher v join fetch v.merchant
            where v.status = :status and v.remaining > 0 and v.saleEndsAt > :now""",
            countQuery = """
                    select count(v) from Voucher v
                    where v.status = :status and v.remaining > 0 and v.saleEndsAt > :now""")
    Page<Voucher> findAvailable(VoucherStatus status, Instant now, Pageable pageable);

    /*
     * takeOne: takes one voucher from stock in a single atomic UPDATE.
     * The database locks the row, so the check (remaining > 0) and the subtraction happen together.
     * Returns 1 if a voucher was taken, 0 if it is sold out, inactive or expired (the service then returns 409).
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update Voucher v set v.remaining = v.remaining - 1
            where v.id = :id and v.remaining > 0 and v.status = :status and v.saleEndsAt > :now""")
    int takeOne(UUID id, VoucherStatus status, Instant now);

    @Query("select v.remaining from Voucher v where v.id = :id")
    int readRemaining(UUID id);

    /*
     * Flush: write my pending changes to the database before the UPDATE runs.
     * Clear: forget cached entities after the UPDATE, so the next read gets fresh data.
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Voucher v set v.remaining = :remaining where v.id = :id")
    void overwriteRemaining(UUID id, int remaining);

    @Modifying
    @Query("delete from Voucher v where v.merchant.id in :userIds")
    void deleteAllByMerchantIdIn(Collection<UUID> userIds);
}
