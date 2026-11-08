package com.almotawaj.wallet.wallet.model.response;

import com.almotawaj.wallet.user.model.UserRole;

public record RecipientSuggestionResponse(String iban, String maskedName, String maskedEmail, String maskedMobile, UserRole role) {
}
