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
    /*
     * EntityManager is the JPA engine that repositories use underneath.
     * It’s needed for refresh(wallet, PESSIMISTIC_WRITE),
     * which locks the row (FOR UPDATE) and reloads it fresh. A repository method could return a stale cached copy
     */
    private final EntityManager entityManager;

    public Wallet lock(UUID walletId) {

        Wallet wallet = entityManager.find(Wallet.class, walletId);
        if (wallet == null) {
            throw new InformationNotFoundException(ErrorMessages.NOT_FOUND);
        }
        entityManager.refresh(wallet, LockModeType.PESSIMISTIC_WRITE);
        return wallet;
    }

    /*
     * Locks both wallets, always the smaller ID first.
     * Why: thread A (wallet 1 pays wallet 2) locks wallet 1 first, while thread B (wallet 2 pays wallet 1)
     * locks wallet 2 first. Now A waits for B to release wallet 2, and B waits for A to release wallet 1.
     * Both wait forever (deadlock). A fixed order means every thread locks in the same sequence.
     * Returns them as source and target, whatever order they were locked in.
     */
    public LockedPair lockPair(UUID sourceWalletId, UUID targetWalletId) {
        boolean sourceFirst = sourceWalletId.compareTo(targetWalletId) < 0;
        Wallet lower = lock(sourceFirst ? sourceWalletId : targetWalletId);
        Wallet higher = lock(sourceFirst ? targetWalletId : sourceWalletId);
        return sourceFirst ? new LockedPair(lower, higher) : new LockedPair(higher, lower);
    }

    public record LockedPair(Wallet source, Wallet target) {
    }
}
