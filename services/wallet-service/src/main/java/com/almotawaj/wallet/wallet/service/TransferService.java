package com.almotawaj.wallet.wallet.service;

import com.almotawaj.wallet.audit.model.AuditAction;
import com.almotawaj.wallet.audit.model.AuditTargetType;
import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.WalletLimits;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.wallet.event.MoneyReceivedEvent;
import com.almotawaj.wallet.wallet.model.Counterparty;
import com.almotawaj.wallet.wallet.model.TransactionType;
import com.almotawaj.wallet.wallet.model.Wallet;
import com.almotawaj.wallet.wallet.model.WalletTransaction;
import com.almotawaj.wallet.wallet.model.request.TransferRequest;
import com.almotawaj.wallet.wallet.model.response.RecipientResponse;
import com.almotawaj.wallet.wallet.model.response.TransferOptionsResponse;
import com.almotawaj.wallet.wallet.model.response.WalletTransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Wallet-to-wallet transfers: recipient preview, limits and the money movement itself.
 */
@Service
@RequiredArgsConstructor
public class TransferService {
    private final WalletProvisioner provisioner;
    private final WalletLocker walletLocker;
    private final RecipientResolver recipientResolver;
    private final WalletHolderNames holderNames;
    private final Counterparties counterparties;
    private final DailyLimitPolicy dailyLimitPolicy;
    private final WalletLedger ledger;
    private final ApplicationEventPublisher eventPublisher;
    private final AuditService auditService;

    /**
     * Finds the wallet holder a sender is about to pay and returns a masked preview.
     *
     * @param senderId the user who is sending
     * @param query    the recipient's email, mobile number or IBAN
     * @return the recipient's masked name, masked email and role
     */
    @Transactional(readOnly = true)
    public RecipientResponse findRecipient(UUID senderId, String query) {
        User recipient = recipientResolver.resolve(senderId, query);
        return new RecipientResponse(holderNames.maskedName(recipient), holderNames.maskedEmail(recipient),
                recipient.getRole());
    }

    /**
     * Returns what the sender can transfer right now.
     *
     * @param senderId the user who is sending
     * @return the balance, the per-transfer range, the daily limit and what is left of it today
     */
    @Transactional
    public TransferOptionsResponse getOptions(UUID senderId) {
        Wallet wallet = provisioner.getOrCreate(senderId);
        return new TransferOptionsResponse(wallet.getBalance(), new BigDecimal(WalletLimits.TRANSFER_MIN),
                new BigDecimal(WalletLimits.TRANSFER_MAX), DailyLimitPolicy.TRANSFER_LIMIT,
                dailyLimitPolicy.remainingTransfer(wallet.getId()));
    }

    /**
     * Moves money from the sender's wallet to the recipient's wallet in one transaction. Both wallet rows
     * are locked in a fixed order, so concurrent transfers can neither overspend nor deadlock.
     *
     * @param senderId the user who is sending
     * @param request  the recipient, the amount and an optional note
     * @return the sender's ledger entry
     * @throws BusinessRuleException if the balance is too low or the daily transfer limit would be exceeded
     */
    @Transactional
    public WalletTransactionResponse transfer(UUID senderId, TransferRequest request) {
        User recipient = recipientResolver.resolve(senderId, request.recipient());
        UUID senderWalletId = provisioner.getOrCreate(senderId).getId();
        UUID recipientWalletId = provisioner.getOrCreate(recipient.getId()).getId();
        WalletLocker.LockedPair locked = walletLocker.lockPair(senderWalletId, recipientWalletId);
        Wallet sender = locked.source();
        Wallet receiver = locked.target();
        BigDecimal amount = request.amount();
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new BusinessRuleException(ErrorMessages.INSUFFICIENT_BALANCE, ErrorCodes.INSUFFICIENT_BALANCE);
        }
        dailyLimitPolicy.ensureTransferAllowed(sender.getId(), amount);
        String note = request.note() == null || request.note().isBlank() ? null : request.note().strip();
        Counterparty senderDetails = counterparties.of(sender);
        WalletTransaction received = ledger.credit(receiver, TransactionType.TRANSFER_IN, amount, senderDetails, note);
        eventPublisher.publishEvent(new MoneyReceivedEvent(recipient.getId(), received.getAmount(), senderDetails.name(),
                received.getReference()));
        WalletTransaction transaction = ledger.debit(sender, TransactionType.TRANSFER_OUT, amount,
                counterparties.of(receiver), note);
        auditService.record(senderId, AuditAction.TRANSFER_COMPLETED, AuditTargetType.WALLET_TRANSACTION,
                transaction.getId(), null);
        return WalletTransactionResponse.from(transaction);
    }
}
