package com.almotawaj.wallet.statistics.model;

import com.almotawaj.wallet.user.model.UserRole;

public record RoleCount(UserRole role, Long total) {
}
