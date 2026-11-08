package com.almotawaj.wallet.wallet.model.response;

import com.almotawaj.wallet.user.model.UserRole;

public record RecipientResponse(String maskedName, String maskedEmail, UserRole role) {
}
