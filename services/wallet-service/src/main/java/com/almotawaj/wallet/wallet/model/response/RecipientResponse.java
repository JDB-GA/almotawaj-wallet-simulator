package com.almotawaj.wallet.wallet.model.response;

import com.almotawaj.wallet.account.model.AccountRole;

public record RecipientResponse(String maskedName, String maskedEmail, AccountRole role) {
}
