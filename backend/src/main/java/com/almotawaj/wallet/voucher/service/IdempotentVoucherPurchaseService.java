package com.almotawaj.wallet.voucher.service;

import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.WalletConstants;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.voucher.model.IdempotencyRecord;
import com.almotawaj.wallet.voucher.model.response.VoucherPurchaseResponse;
import com.almotawaj.wallet.voucher.repository.IdempotencyRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class IdempotentVoucherPurchaseService {
    private final VoucherPurchaseService purchaseService;
    private final IdempotencyRecordRepository idempotencyRepository;
    private final ConcurrentMap<UUID, ReentrantLock> voucherLocks = new ConcurrentHashMap<>();

    public VoucherPurchaseResponse purchase(UUID buyerId, UUID voucherId, String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) { // We check if the idempotency key is null or not to prevent an unnecessary call to find an existing idempotency record
            return buy(buyerId, voucherId, null);
        }
        String key = idempotencyKey.strip();
        Optional<IdempotencyRecord> existing = find(buyerId, key);
        if (existing.isPresent()) { // If present, then we just return the saved record; you don't have to recreate it
            return replay(buyerId, voucherId, existing.get());
        }
        try {
            return buy(buyerId, voucherId, key); // If the idempotency key is new (not used yet), then buy from scratch
        } catch (DataIntegrityViolationException e) {
            return find(buyerId, key).map(record -> replay(buyerId, voucherId, record)).orElseThrow(() -> e);
        }
    }

    private VoucherPurchaseResponse buy(UUID buyerId, UUID voucherId, String key) {
        ReentrantLock lock = voucherLocks.computeIfAbsent(voucherId, id -> new ReentrantLock());
        // Make a lock for this specific voucher ID so only one buyer at a time purchases it (on this server only)
        lock.lock();
        try {
            // Do the purchase using those
            return purchaseService.purchase(buyerId, voucherId, key);
        } finally {
            // Unlock after finalizing the purchase for this specific ID
            lock.unlock();
        }
    }

    // Find the key here, whether it exists or not
    private Optional<IdempotencyRecord> find(UUID buyerId, String key) {
        return idempotencyRepository.findByUserIdAndKey(buyerId, key);
    }

    // Simply return the record of the voucher purchase (simply the details of a purchase of a voucher)
    private VoucherPurchaseResponse replay(UUID buyerId, UUID voucherId, IdempotencyRecord record) {
        if (!WalletConstants.IDEMPOTENCY_VOUCHER_PURCHASE.equals(record.getEndpoint())) {
            throw new BusinessRuleException(ErrorMessages.IDEMPOTENCY_KEY_REUSED, ErrorCodes.IDEMPOTENCY_KEY_REUSED);
        }
        return purchaseService.replay(buyerId, voucherId, record);
    }
}
