package com.almotawaj.wallet.wallet.service;

import com.almotawaj.wallet.account.model.AccountHolder;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import com.almotawaj.wallet.config.constants.WalletConstants;
import org.springframework.stereotype.Component;

@Component
public class WalletHolderNames {
    public String fullName(AccountHolder user) {
        String name = user.getKycFullName() != null ? user.getKycFullName()
                : user.getDisplayName() != null ? user.getDisplayName() : user.getEmailAddress();
        return name.length() <= ValidationLimits.COUNTERPARTY_NAME_MAX
                ? name
                : name.substring(0, ValidationLimits.COUNTERPARTY_NAME_MAX);
    }

    public String maskedName(AccountHolder user) {
        String[] parts = fullName(user).trim().split("\\s+");
        if (parts.length < 2 || parts[0].contains(WalletConstants.EMAIL_MARKER)) {
            return maskedEmail(user);
        }
        return parts[0] + " " + parts[parts.length - 1].charAt(0) + WalletConstants.NAME_INITIAL_SUFFIX;
    }

    public String maskedEmail(AccountHolder user) {
        String email = user.getEmailAddress();
        int at = email.indexOf(WalletConstants.EMAIL_MARKER);
        return email.charAt(0) + WalletConstants.EMAIL_MASK + email.substring(at);
    }

    public String maskedMobile(AccountHolder user) {
        String mobile = user.getMobileNumber();
        int visible = Math.min(WalletConstants.MOBILE_VISIBLE_DIGITS, mobile.length());
        return "*".repeat(mobile.length() - visible) + mobile.substring(mobile.length() - visible);
    }
}
