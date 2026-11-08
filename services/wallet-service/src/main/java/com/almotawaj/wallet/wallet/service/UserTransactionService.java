package com.almotawaj.wallet.wallet.service;

import com.almotawaj.wallet.common.model.response.PageResponse;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.WalletConstants;
import com.almotawaj.wallet.exception.InformationNotFoundException;
import com.almotawaj.wallet.user.repository.UserRepository;
import com.almotawaj.wallet.wallet.model.request.TransactionSearchRequest;
import com.almotawaj.wallet.wallet.model.response.WalletTransactionResponse;
import com.almotawaj.wallet.wallet.repository.WalletTransactionRepository;
import com.almotawaj.wallet.wallet.repository.specification.WalletTransactionSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserTransactionService {
    private static final ZoneId ZONE = ZoneId.of(WalletConstants.TIME_ZONE);

    private final UserRepository userRepository;
    private final WalletTransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public PageResponse<WalletTransactionResponse> list(UUID userId, TransactionSearchRequest filter, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new InformationNotFoundException(ErrorMessages.USER_NOT_FOUND);
        }
        return PageResponse.from(
                transactionRepository.findAll(WalletTransactionSpecifications.forUser(userId, filter, ZONE), pageable),
                WalletTransactionResponse::from);
    }
}
