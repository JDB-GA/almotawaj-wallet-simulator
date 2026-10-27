package com.almotawaj.wallet.user.service;

import com.almotawaj.wallet.kyc.model.KycApplication;
import com.almotawaj.wallet.kyc.repository.KycApplicationRepository;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.response.AdminUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminUserMapper {
    private final KycApplicationRepository applicationRepository;

    public AdminUserResponse toResponse(User user) {
        String fullName = applicationRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId())
                .map(KycApplication::getFullName)
                .orElse(null);
        return AdminUserResponse.from(user, fullName);
    }
}
