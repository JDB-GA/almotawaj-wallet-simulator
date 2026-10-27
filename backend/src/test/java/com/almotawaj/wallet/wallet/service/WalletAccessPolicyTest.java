package com.almotawaj.wallet.wallet.service;

import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.kyc.model.KycStatus;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WalletAccessPolicyTest {
    private final WalletAccessPolicy policy = new WalletAccessPolicy();

    private static User user(UserRole role, KycStatus kycStatus) {
        User user = new User();
        user.setRole(role);
        user.setKycStatus(kycStatus);
        return user;
    }

    @Test
    void allowsApprovedClientAndMerchant() {
        assertThatCode(() -> policy.ensureCanUseWallet(user(UserRole.CLIENT, KycStatus.APPROVED))).doesNotThrowAnyException();
        assertThatCode(() -> policy.ensureCanUseWallet(user(UserRole.MERCHANT, KycStatus.NOT_SUBMITTED))).doesNotThrowAnyException();
    }

    @Test
    void rejectsClientWithoutApprovedKyc() {
        assertThatThrownBy(() -> policy.ensureCanUseWallet(user(UserRole.CLIENT, KycStatus.PENDING)))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("code", ErrorCodes.WALLET_KYC_REQUIRED);
    }

    @Test
    void rejectsAdmins() {
        assertThatThrownBy(() -> policy.ensureCanUseWallet(user(UserRole.ADMIN, KycStatus.NOT_SUBMITTED)))
                .isInstanceOf(AccessDeniedException.class);
    }
}
