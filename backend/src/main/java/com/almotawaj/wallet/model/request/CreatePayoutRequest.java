package com.almotawaj.wallet.model.request;

import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.WalletLimits;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreatePayoutRequest(
        @NotEmpty(message = ErrorMessages.PAYOUT_ITEMS_REQUIRED)
        @Size(max = WalletLimits.PAYOUT_ITEMS_MAX, message = ErrorMessages.PAYOUT_ITEMS_TOO_MANY)
        List<@Valid PayoutItemRequest> items
) {
}
