package com.almotawaj.wallet.auth.model;

import com.almotawaj.wallet.user.model.response.UserResponse;

public record LoginResult(String token, UserResponse user) {
}
