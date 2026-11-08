package com.almotawaj.wallet.identity.auth.model;

import com.almotawaj.wallet.identity.user.model.response.UserResponse;

public record LoginResult(String token, UserResponse user) {
}
