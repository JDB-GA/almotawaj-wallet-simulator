package com.almotawaj.wallet.seed.service;

import com.almotawaj.wallet.checkout.repository.CheckoutSessionRepository;
import com.almotawaj.wallet.checkout.repository.MerchantApiKeyRepository;
import com.almotawaj.wallet.checkout.repository.MerchantWebhookRepository;
import com.almotawaj.wallet.config.constants.LogMessages;
import com.almotawaj.wallet.paymentrequest.repository.PaymentRequestRepository;
import com.almotawaj.wallet.payout.repository.PayoutBatchRepository;
import com.almotawaj.wallet.payout.repository.PayoutItemRepository;
import com.almotawaj.wallet.voucher.repository.IdempotencyRecordRepository;
import com.almotawaj.wallet.voucher.repository.VoucherPurchaseRepository;
import com.almotawaj.wallet.voucher.repository.VoucherRepository;
import com.almotawaj.wallet.wallet.repository.WalletRepository;
import com.almotawaj.wallet.wallet.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeedDataCleanupService {
    private final WalletTransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final PaymentRequestRepository paymentRequestRepository;
    private final CheckoutSessionRepository checkoutSessionRepository;
    private final MerchantApiKeyRepository apiKeyRepository;
    private final MerchantWebhookRepository webhookRepository;
    private final IdempotencyRecordRepository idempotencyRepository;
    private final VoucherPurchaseRepository voucherPurchaseRepository;
    private final VoucherRepository voucherRepository;
    private final PayoutItemRepository payoutItemRepository;
    private final PayoutBatchRepository payoutBatchRepository;

    @Transactional
    public void clean(List<UUID> userIds) {
        paymentRequestRepository.deleteAllByUserIdIn(userIds);
        idempotencyRepository.deleteAllByUserIdIn(userIds);
        voucherPurchaseRepository.deleteAllByUserIdIn(userIds);
        checkoutSessionRepository.deleteAllByUserIdIn(userIds);
        voucherRepository.deleteAllByMerchantIdIn(userIds);
        payoutItemRepository.deleteAllByUserIdIn(userIds);
        payoutBatchRepository.deleteAllByMerchantIdIn(userIds);
        apiKeyRepository.deleteAllByMerchantIdIn(userIds);
        webhookRepository.deleteAllByMerchantIdIn(userIds);
        transactionRepository.deleteAllByWalletUserIdIn(userIds);
        walletRepository.deleteAllByUserIdIn(userIds);
        log.info(LogMessages.SEED_DATA_CLEANED, userIds.size());
    }
}
