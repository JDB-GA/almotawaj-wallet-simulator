package com.almotawaj.wallet.identity.statistics;

import com.almotawaj.wallet.identity.config.constants.ApiPaths;
import com.almotawaj.wallet.identity.config.constants.SecurityConstants;
import com.almotawaj.wallet.identity.config.constants.docs.AdminStatisticsDocs;
import com.almotawaj.wallet.identity.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.identity.user.model.UserRole;
import com.almotawaj.wallet.identity.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.EnumMap;
import java.util.Map;

@RestController
@Tag(name = AdminStatisticsDocs.TAG, description = AdminStatisticsDocs.TAG_DESCRIPTION)
@RequestMapping(ApiPaths.ADMIN_STATISTICS)
@PreAuthorize(SecurityConstants.HAS_STATISTICS_VIEW)
@RequiredArgsConstructor
public class UserStatisticsController {
    private final UserRepository userRepository;

    @Operation(summary = AdminStatisticsDocs.USERS, description = AdminStatisticsDocs.USERS_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = AdminStatisticsDocs.USERS_OK)
    @GetMapping(ApiPaths.USER_STATISTICS)
    @Transactional(readOnly = true)
    public UserStatisticsResponse getUserStatistics() {
        Map<UserRole, Long> usersByRole = new EnumMap<>(UserRole.class);
        for (UserRole role : UserRole.values()) {
            usersByRole.put(role, 0L);
        }
        userRepository.countByRole().forEach(count -> usersByRole.put(count.role(), count.total()));
        long totalUsers = usersByRole.values().stream().mapToLong(Long::longValue).sum();
        return new UserStatisticsResponse(totalUsers, usersByRole);
    }
}
