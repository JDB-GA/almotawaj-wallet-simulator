package com.almotawaj.wallet.seed.service;

import com.almotawaj.wallet.audit.repository.AuditLogRepository;
import com.almotawaj.wallet.auth.repository.OtpChallengeRepository;
import com.almotawaj.wallet.checkout.repository.CheckoutSessionRepository;
import com.almotawaj.wallet.checkout.repository.MerchantApiKeyRepository;
import com.almotawaj.wallet.checkout.repository.MerchantWebhookRepository;
import com.almotawaj.wallet.common.service.FileStorageService;
import com.almotawaj.wallet.config.constants.LogMessages;
import com.almotawaj.wallet.config.constants.SeedConstants;
import com.almotawaj.wallet.kyc.repository.KycApplicationRepository;
import com.almotawaj.wallet.kyc.repository.KycDocumentRepository;
import com.almotawaj.wallet.paymentrequest.repository.PaymentRequestRepository;
import com.almotawaj.wallet.payout.repository.PayoutBatchRepository;
import com.almotawaj.wallet.payout.repository.PayoutItemRepository;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.repository.UserRepository;
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
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeedDataCleanupService {
    private final UserRepository userRepository;
    private final WalletTransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final KycApplicationRepository applicationRepository;
    private final KycDocumentRepository documentRepository;
    private final OtpChallengeRepository otpChallengeRepository;
    private final PaymentRequestRepository paymentRequestRepository;
    private final CheckoutSessionRepository checkoutSessionRepository;
    private final MerchantApiKeyRepository apiKeyRepository;
    private final MerchantWebhookRepository webhookRepository;
    private final AuditLogRepository auditLogRepository;
    private final IdempotencyRecordRepository idempotencyRepository;
    private final VoucherPurchaseRepository voucherPurchaseRepository;
    private final VoucherRepository voucherRepository;
    private final PayoutItemRepository payoutItemRepository;
    private final PayoutBatchRepository payoutBatchRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public void clean() {
        List<User> users = userRepository.findAllByEmailAddressIn(SeedConstants.accountEmails());
        List<UUID> userIds = users.stream().map(User::getId).toList();
        if (userIds.isEmpty()) {
            return;
        }

        users.stream().map(User::getPictureKey).filter(Objects::nonNull).forEach(fileStorageService::deleteAfterCommit);
        documentRepository.findAllByApplicationUserIdIn(userIds)
                .forEach(document -> fileStorageService.deleteAfterCommit(document.getStorageKey()));
        auditLogRepository.clearActorsByIdIn(userIds);
        applicationRepository.clearReviewersByIdIn(userIds);
        otpChallengeRepository.deleteAllByUserIdIn(userIds);
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
        documentRepository.deleteAllByApplicationUserIdIn(userIds);
        applicationRepository.deleteAllByUserIdIn(userIds);
        users.forEach(user -> user.getPermissions().clear());
        userRepository.flush();
        userRepository.deleteAllInBatch(users);
        log.info(LogMessages.SEED_DATA_CLEANED, users.size());
    }
}
