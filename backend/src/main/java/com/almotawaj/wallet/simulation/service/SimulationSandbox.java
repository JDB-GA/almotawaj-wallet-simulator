package com.almotawaj.wallet.simulation.service;

import com.almotawaj.wallet.audit.repository.AuditLogRepository;
import com.almotawaj.wallet.config.constants.SimulationConstants;
import com.almotawaj.wallet.kyc.model.KycStatus;
import com.almotawaj.wallet.payout.model.PayoutBatch;
import com.almotawaj.wallet.payout.model.PayoutItem;
import com.almotawaj.wallet.payout.repository.PayoutBatchRepository;
import com.almotawaj.wallet.payout.repository.PayoutItemRepository;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.UserRole;
import com.almotawaj.wallet.user.repository.UserRepository;
import com.almotawaj.wallet.voucher.model.Voucher;
import com.almotawaj.wallet.voucher.repository.IdempotencyRecordRepository;
import com.almotawaj.wallet.voucher.repository.VoucherPurchaseRepository;
import com.almotawaj.wallet.voucher.repository.VoucherRepository;
import com.almotawaj.wallet.wallet.model.Wallet;
import com.almotawaj.wallet.wallet.repository.WalletRepository;
import com.almotawaj.wallet.wallet.repository.WalletTransactionRepository;
import com.almotawaj.wallet.wallet.service.IbanGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SimulationSandbox {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int DECIMAL_RADIX = 10;

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final VoucherRepository voucherRepository;
    private final VoucherPurchaseRepository purchaseRepository;
    private final IdempotencyRecordRepository idempotencyRepository;
    private final PayoutBatchRepository batchRepository;
    private final PayoutItemRepository itemRepository;
    private final AuditLogRepository auditLogRepository;
    private final IbanGenerator ibanGenerator;
    private final Clock clock;

    @Transactional
    public User createMerchant(BigDecimal balance) {
        User merchant = user(UserRole.MERCHANT);
        merchant.setDisplayName(SimulationConstants.MERCHANT_NAME);
        wallet(merchant, balance);
        return merchant;
    }

    @Transactional
    public List<User> createClients(int count, BigDecimal balance) {
        List<User> clients = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            User client = user(UserRole.CLIENT);
            wallet(client, balance);
            clients.add(client);
        }
        return clients;
    }

    @Transactional
    public UUID createVoucher(User merchant, int stock) {
        Voucher voucher = new Voucher();
        voucher.setMerchant(merchant);
        voucher.setTitle(SimulationConstants.VOUCHER_TITLE);
        voucher.setPrice(new BigDecimal(SimulationConstants.VOUCHER_PRICE));
        voucher.setFaceValue(new BigDecimal(SimulationConstants.VOUCHER_FACE_VALUE));
        voucher.setTotalQuantity(stock);
        voucher.setRemaining(stock);
        voucher.setPerClientLimit(stock);
        voucher.setSaleEndsAt(clock.instant().plus(Duration.ofHours(SimulationConstants.VOUCHER_EXPIRY_HOURS)));
        return voucherRepository.saveAndFlush(voucher).getId();
    }

    @Transactional
    public List<UUID> createPayoutBatch(User merchant, List<User> recipients, BigDecimal amount) {
        PayoutBatch batch = new PayoutBatch();
        batch.setMerchant(merchant);
        batch.setItemCount(recipients.size());
        batch.setTotalAmount(amount.multiply(BigDecimal.valueOf(recipients.size())));
        batchRepository.save(batch);
        List<UUID> ids = new ArrayList<>();
        ids.add(batch.getId());
        for (User recipient : recipients) {
            PayoutItem item = new PayoutItem();
            item.setBatch(batch);
            item.setRecipient(recipient);
            item.setAmount(amount);
            ids.add(itemRepository.save(item).getId());
        }
        batchRepository.flush();
        return ids;
    }

    @Transactional(readOnly = true)
    public int remaining(UUID voucherId) {
        return voucherRepository.readRemaining(voucherId);
    }

    @Transactional(readOnly = true)
    public BigDecimal balance(UUID userId) {
        return walletRepository.readBalance(userId);
    }

    @Transactional
    public void cleanUp(Collection<UUID> userIds) {
        idempotencyRepository.deleteAllByUserIdIn(userIds);
        purchaseRepository.deleteAllByUserIdIn(userIds);
        voucherRepository.deleteAllByMerchantIdIn(userIds);
        itemRepository.deleteAllByUserIdIn(userIds);
        batchRepository.deleteAllByMerchantIdIn(userIds);
        transactionRepository.deleteAllByWalletUserIdIn(userIds);
        walletRepository.deleteAllByUserIdIn(userIds);
        auditLogRepository.deleteAllByActorIdIn(userIds);
        userRepository.deleteAllByIdInBatch(userIds);
    }

    private User user(UserRole role) {
        User user = new User();
        user.setEmailAddress(SimulationConstants.EMAIL_PATTERN.formatted(UUID.randomUUID()));
        user.setMobileNumber(randomMobile());
        user.setPassword(SimulationConstants.DISABLED_PASSWORD);
        user.setRole(role);
        user.setEmailVerified(true);
        user.setKycStatus(role == UserRole.CLIENT ? KycStatus.APPROVED : KycStatus.NOT_SUBMITTED);
        return userRepository.save(user);
    }

    private void wallet(User owner, BigDecimal balance) {
        Wallet wallet = new Wallet();
        wallet.setUser(owner);
        wallet.setIban(ibanGenerator.generate());
        wallet.setBalance(balance);
        walletRepository.save(wallet);
    }

    private static String randomMobile() {
        StringBuilder mobile = new StringBuilder(SimulationConstants.MOBILE_PREFIX);
        for (int index = 0; index < SimulationConstants.MOBILE_RANDOM_DIGITS; index++) {
            mobile.append(RANDOM.nextInt(DECIMAL_RADIX));
        }
        return mobile.toString();
    }
}
