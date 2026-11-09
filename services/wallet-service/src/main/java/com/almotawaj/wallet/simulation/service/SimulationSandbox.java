package com.almotawaj.wallet.simulation.service;

import com.almotawaj.wallet.account.model.AccountHolder;
import com.almotawaj.wallet.account.model.AccountRole;
import com.almotawaj.wallet.account.repository.AccountHolderRepository;
import com.almotawaj.wallet.config.constants.SimulationConstants;
import com.almotawaj.wallet.payout.model.PayoutBatch;
import com.almotawaj.wallet.payout.model.PayoutItem;
import com.almotawaj.wallet.payout.repository.PayoutBatchRepository;
import com.almotawaj.wallet.payout.repository.PayoutItemRepository;
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
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SimulationSandbox {
    private final AccountHolderRepository accountRepository;
    private final SandboxAccountsClient sandboxAccounts;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final VoucherRepository voucherRepository;
    private final VoucherPurchaseRepository purchaseRepository;
    private final IdempotencyRecordRepository idempotencyRepository;
    private final PayoutBatchRepository batchRepository;
    private final PayoutItemRepository itemRepository;
    private final IbanGenerator ibanGenerator;
    private final Clock clock;

    public AccountHolder createMerchant(BigDecimal balance) {
        return accounts(AccountRole.MERCHANT, 1, SimulationConstants.MERCHANT_NAME, balance).get(0);
    }

    public List<AccountHolder> createClients(int count, BigDecimal balance) {
        return accounts(AccountRole.CLIENT, count, null, balance);
    }

    private List<AccountHolder> accounts(AccountRole role, int count, String displayName, BigDecimal balance) {
        List<AccountHolder> accounts = accountRepository.findAllById(sandboxAccounts.create(role, count, displayName));
        accounts.forEach(account -> wallet(account, balance));
        return accounts;
    }

    @Transactional
    public UUID createVoucher(AccountHolder merchant, int stock) {
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
    public List<UUID> createPayoutBatch(AccountHolder merchant, List<AccountHolder> recipients, BigDecimal amount) {
        PayoutBatch batch = new PayoutBatch();
        batch.setMerchant(merchant);
        batch.setItemCount(recipients.size());
        batch.setTotalAmount(amount.multiply(BigDecimal.valueOf(recipients.size())));
        batchRepository.save(batch);
        List<UUID> ids = new ArrayList<>();
        ids.add(batch.getId());
        for (AccountHolder recipient : recipients) {
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
    }

    private void wallet(AccountHolder owner, BigDecimal balance) {
        Wallet wallet = new Wallet();
        wallet.setUser(owner);
        wallet.setIban(ibanGenerator.generate());
        wallet.setBalance(balance);
        walletRepository.save(wallet);
    }

}
