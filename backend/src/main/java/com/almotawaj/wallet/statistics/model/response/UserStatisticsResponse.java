package com.almotawaj.wallet.statistics.model.response;

import com.almotawaj.wallet.user.model.UserRole;

import java.util.Map;

public record UserStatisticsResponse(long totalUsers, Map<UserRole, Long> usersByRole) {
}
