package com.almotawaj.wallet.wallet.service;

import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.ValidationPatterns;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.exception.InformationNotFoundException;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final WalletAccessPolicy accessPolicy;

    public User resolve(UUID senderId, String query) {
        User recipient = find(query.trim()).orElseThrow(() ->
                new InformationNotFoundException(ErrorMessages.RECIPIENT_NOT_FOUND, ErrorCodes.RECIPIENT_NOT_FOUND));
        if (recipient.getId().equals(senderId)) {
            throw new BusinessRuleException(ErrorMessages.SELF_TRANSFER_NOT_ALLOWED, ErrorCodes.SELF_TRANSFER_NOT_ALLOWED);
        }
        if (!accessPolicy.canReceive(recipient)) {
            throw new BusinessRuleException(ErrorMessages.RECIPIENT_UNAVAILABLE, ErrorCodes.RECIPIENT_UNAVAILABLE);
        }
        return recipient;
    }

    private Optional<User> find(String query) {
        if (LoginIdentifier.isEmail(query)) {
            return userRepository.findByEmailAddress(LoginIdentifier.normalizeEmail(query));
        }
        if (query.matches(ValidationPatterns.MOBILE_NUMBER)) {
            return userRepository.findByMobileNumber(LoginIdentifier.normalizeMobile(query));
        }
        return Iban.isValid(query) ? walletRepository.findByIban(Iban.normalize(query)).map(Wallet::getUser) : Optional.empty();
    }
}
