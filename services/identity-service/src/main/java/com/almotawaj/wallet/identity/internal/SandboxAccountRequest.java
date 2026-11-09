package com.almotawaj.wallet.identity.internal;

import com.almotawaj.wallet.identity.user.model.UserRole;

public record SandboxAccountRequest(UserRole role, int count, String displayName) {
}
