package com.almotawaj.wallet.wallet.service;

import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.exception.InformationNotFoundException;
import com.almotawaj.wallet.wallet.model.Wallet;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WalletLocker {
    private final EntityManager entityManager;

    public Wallet lock(UUID walletId) {
        Wallet wallet = entityManager.find(Wallet.class, walletId);
        if (wallet == null) {
            throw new InformationNotFoundException(ErrorMessages.NOT_FOUND);
        }
        entityManager.refresh(wallet, LockModeType.PESSIMISTIC_WRITE);
        return wallet;
    }

    public LockedPair lockPair(UUID sourceWalletId, UUID targetWalletId) {
        boolean sourceFirst = sourceWalletId.compareTo(targetWalletId) < 0;
        Wallet lower = lock(sourceFirst ? sourceWalletId : targetWalletId);
        Wallet higher = lock(sourceFirst ? targetWalletId : sourceWalletId);
        return sourceFirst ? new LockedPair(lower, higher) : new LockedPair(higher, lower);
    }

    public record LockedPair(Wallet source, Wallet target) {
    }
}
