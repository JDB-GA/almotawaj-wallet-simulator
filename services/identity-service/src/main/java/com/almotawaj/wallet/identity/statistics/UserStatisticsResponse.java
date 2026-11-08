package com.almotawaj.wallet.identity.statistics;

import com.almotawaj.wallet.identity.user.model.UserRole;

import java.util.Map;

public record UserStatisticsResponse(long totalUsers, Map<UserRole, Long> usersByRole) {
}
