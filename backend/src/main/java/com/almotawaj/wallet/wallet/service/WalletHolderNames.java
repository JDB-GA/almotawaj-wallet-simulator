package com.almotawaj.wallet.wallet.service;

import com.almotawaj.wallet.config.constants.ValidationLimits;
import com.almotawaj.wallet.config.constants.WalletConstants;
import com.almotawaj.wallet.kyc.model.KycApplication;
import com.almotawaj.wallet.kyc.model.KycApplicationStatus;
import com.almotawaj.wallet.kyc.repository.KycApplicationRepository;
import com.almotawaj.wallet.user.model.User;
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

    public String maskedName(User user) {
        String[] parts = fullName(user).trim().split("\\s+");
        if (parts.length < 2 || parts[0].contains(WalletConstants.EMAIL_MARKER)) {
            return maskedEmail(user);
        }
        return parts[0] + " " + parts[parts.length - 1].charAt(0) + WalletConstants.NAME_INITIAL_SUFFIX;
    }

    public String maskedEmail(User user) {
        String email = user.getEmailAddress();
        int at = email.indexOf(WalletConstants.EMAIL_MARKER);
        return email.charAt(0) + WalletConstants.EMAIL_MASK + email.substring(at);
    }

    public String maskedMobile(User user) {
        String mobile = user.getMobileNumber();
        int visible = Math.min(WalletConstants.MOBILE_VISIBLE_DIGITS, mobile.length());
        return "*".repeat(mobile.length() - visible) + mobile.substring(mobile.length() - visible);
    }
}
