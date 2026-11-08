package com.almotawaj.wallet.identity.user.service;

import com.almotawaj.wallet.identity.config.constants.ValidationLimits;
import com.almotawaj.wallet.identity.kyc.model.KycApplication;
import com.almotawaj.wallet.identity.kyc.model.KycApplicationStatus;
import com.almotawaj.wallet.identity.kyc.repository.KycApplicationRepository;
import com.almotawaj.wallet.identity.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WalletHolderNames {
    private final KycApplicationRepository kycApplicationRepository;

    public String fullName(User user) {
        String name = kycApplicationRepository
                .findFirstByUserIdAndStatusOrderByCreatedAtDesc(user.getId(), KycApplicationStatus.APPROVED)
                .map(KycApplication::getFullName)
                .orElse(user.getDisplayName() == null ? user.getEmailAddress() : user.getDisplayName());
        return name.length() <= ValidationLimits.COUNTERPARTY_NAME_MAX
                ? name
                : name.substring(0, ValidationLimits.COUNTERPARTY_NAME_MAX);
    }
}
