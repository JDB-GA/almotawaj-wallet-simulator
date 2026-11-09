package com.almotawaj.wallet.wallet.service;

import com.almotawaj.wallet.account.model.AccountHolder;
import com.almotawaj.wallet.account.service.AccountHolderService;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.ValidationPatterns;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.exception.InformationNotFoundException;
import com.almotawaj.wallet.util.Iban;
import com.almotawaj.wallet.util.LoginIdentifier;
import com.almotawaj.wallet.wallet.model.Wallet;
import com.almotawaj.wallet.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RecipientResolver {
    private final AccountHolderService accountHolderService;
    private final WalletRepository walletRepository;
    private final WalletAccessPolicy accessPolicy;

    public AccountHolder resolve(UUID senderId, String query) {
        AccountHolder recipient = find(query.trim()).orElseThrow(() ->
                new InformationNotFoundException(ErrorMessages.RECIPIENT_NOT_FOUND, ErrorCodes.RECIPIENT_NOT_FOUND));
        if (recipient.getId().equals(senderId)) {
            throw new BusinessRuleException(ErrorMessages.SELF_TRANSFER_NOT_ALLOWED, ErrorCodes.SELF_TRANSFER_NOT_ALLOWED);
        }
        if (!accessPolicy.canReceive(recipient)) {
            throw new BusinessRuleException(ErrorMessages.RECIPIENT_UNAVAILABLE, ErrorCodes.RECIPIENT_UNAVAILABLE);
        }
        return recipient;
    }

    private Optional<AccountHolder> find(String query) {
        if (LoginIdentifier.isEmail(query) || query.matches(ValidationPatterns.MOBILE_NUMBER)) {
            return accountHolderService.findByIdentifier(query);
        }
        return Iban.isValid(query) ? walletRepository.findByIban(Iban.normalize(query)).map(Wallet::getUser) : Optional.empty();
    }
}
