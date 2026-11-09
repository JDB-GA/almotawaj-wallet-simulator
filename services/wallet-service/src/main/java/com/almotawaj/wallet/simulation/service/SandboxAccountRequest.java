package com.almotawaj.wallet.simulation.service;

import com.almotawaj.wallet.account.model.AccountRole;

public record SandboxAccountRequest(AccountRole role, int count, String displayName) {
}
