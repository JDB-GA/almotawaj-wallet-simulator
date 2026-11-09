package com.almotawaj.wallet.wallet.service;

import com.almotawaj.wallet.account.model.AccountHolder;
import com.almotawaj.wallet.account.repository.AccountHolderRepository;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.ValidationPatterns;
import com.almotawaj.wallet.config.constants.WalletConstants;
import com.almotawaj.wallet.exception.InformationNotFoundException;
import com.almotawaj.wallet.wallet.model.Wallet;
import com.almotawaj.wallet.wallet.model.response.RecipientSuggestionResponse;
import com.almotawaj.wallet.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecipientSuggestionService {
    private final AccountHolderRepository accountRepository;
    private final WalletRepository walletRepository;
    private final WalletAccessPolicy accessPolicy;
    private final WalletHolderNames holderNames;

    @Transactional(readOnly = true)
    public List<RecipientSuggestionResponse> suggest(UUID senderId, String query) {
        AccountHolder sender = accountRepository.findById(senderId)
                .orElseThrow(() -> new InformationNotFoundException(ErrorMessages.USER_NOT_FOUND));
        accessPolicy.ensureCanUseWallet(sender);
        String prefix = query.trim().toLowerCase(Locale.ROOT);
        if (prefix.length() < WalletConstants.SUGGESTION_MIN_QUERY) {
            return List.of();
        }
        String likePrefix = escapeLike(prefix) + WalletConstants.LIKE_SUFFIX;
        String mobilePrefix = escapeLike(ValidationPatterns.MOBILE_COUNTRY_CODE + prefix) + WalletConstants.LIKE_SUFFIX;
        return walletRepository.findRecipientSuggestions(senderId, likePrefix, mobilePrefix,
                        PageRequest.of(0, WalletConstants.SUGGESTION_LIMIT))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private RecipientSuggestionResponse toResponse(Wallet wallet) {
        AccountHolder user = wallet.getUser();
        return new RecipientSuggestionResponse(wallet.getIban(), holderNames.maskedName(user), holderNames.maskedEmail(user),
                holderNames.maskedMobile(user), user.getRole());
    }

    private static String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
