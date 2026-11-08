package com.almotawaj.wallet.wallet.model.response;

import com.almotawaj.wallet.account.model.AccountRole;

public record RecipientSuggestionResponse(String iban, String maskedName, String maskedEmail, String maskedMobile, AccountRole role) {
}
