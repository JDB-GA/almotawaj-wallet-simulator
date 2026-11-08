package com.almotawaj.wallet.wallet.service;

import com.almotawaj.wallet.account.model.AccountHolder;
import com.almotawaj.wallet.account.model.AccountRole;
import com.almotawaj.wallet.account.model.AccountStatus;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.exception.InformationNotFoundException;
import com.almotawaj.wallet.wallet.model.Wallet;
import com.almotawaj.wallet.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** Who may use a wallet. Every client and merchant has one from registration; clients need KYC to use it. */
@Component
@RequiredArgsConstructor
public class WalletAccessPolicy {
    private final WalletRepository walletRepository;

    public boolean canReceive(AccountHolder account) {
        boolean eligibleRole = account.getRole() == AccountRole.MERCHANT
                || (account.getRole() == AccountRole.CLIENT && account.isKycApproved());
        return eligibleRole && account.getStatus() == AccountStatus.ACTIVE;
    }

    /** Returns the user's wallet after checking they may use it. */
    public Wallet usableWallet(UUID userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new InformationNotFoundException(ErrorMessages.WALLET_NOT_FOUND));
        ensureCanUseWallet(wallet.getUser());
        return wallet;
    }

    public void ensureCanUseWallet(AccountHolder account) {
        if (account.getRole() == AccountRole.MERCHANT) {
            return;
        }
        if (account.getRole() != AccountRole.CLIENT) {
            throw new AccessDeniedException(ErrorMessages.ACCESS_DENIED);
        }
        if (!account.isKycApproved()) {
            throw new BusinessRuleException(ErrorMessages.WALLET_KYC_REQUIRED, ErrorCodes.WALLET_KYC_REQUIRED);
        }
    }
}
