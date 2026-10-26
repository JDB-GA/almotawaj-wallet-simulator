package com.almotawaj.wallet.service;

import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.WalletConstants;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.model.IdempotencyRecord;
import com.almotawaj.wallet.model.response.VoucherPurchaseResponse;
import com.almotawaj.wallet.repository.IdempotencyRecordRepository;
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
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return buy(buyerId, voucherId, null);
        }
        String key = idempotencyKey.strip();
        Optional<IdempotencyRecord> existing = find(buyerId, key);
        if (existing.isPresent()) {
            return replay(buyerId, voucherId, existing.get());
        }
        try {
            return buy(buyerId, voucherId, key);
        } catch (DataIntegrityViolationException e) {
            return find(buyerId, key).map(record -> replay(buyerId, voucherId, record)).orElseThrow(() -> e);
        }
    }

    private VoucherPurchaseResponse buy(UUID buyerId, UUID voucherId, String key) {
        ReentrantLock lock = voucherLocks.computeIfAbsent(voucherId, id -> new ReentrantLock());
        lock.lock();
        try {
            return purchaseService.purchase(buyerId, voucherId, key);
        } finally {
            lock.unlock();
        }
    }

    private Optional<IdempotencyRecord> find(UUID buyerId, String key) {
        return idempotencyRepository.findByUserIdAndKey(buyerId, key);
    }

    private VoucherPurchaseResponse replay(UUID buyerId, UUID voucherId, IdempotencyRecord record) {
        if (!WalletConstants.IDEMPOTENCY_VOUCHER_PURCHASE.equals(record.getEndpoint())) {
            throw new BusinessRuleException(ErrorMessages.IDEMPOTENCY_KEY_REUSED, ErrorCodes.IDEMPOTENCY_KEY_REUSED);
        }
        return purchaseService.replay(buyerId, voucherId, record);
    }
}
