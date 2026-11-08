package com.almotawaj.wallet.wallet.service;

import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.account.model.AccountHolder;
import com.almotawaj.wallet.account.model.AccountRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WalletAccessPolicyTest {
    private final WalletAccessPolicy policy = new WalletAccessPolicy(org.mockito.Mockito.mock(com.almotawaj.wallet.wallet.repository.WalletRepository.class));

    private static AccountHolder user(AccountRole role, boolean kycApproved) {
        AccountHolder user = new AccountHolder();
        user.setRole(role);
        user.setKycApproved(kycApproved);
        return user;
    }

    @Test
    void allowsApprovedClientAndMerchant() {
        assertThatCode(() -> policy.ensureCanUseWallet(user(AccountRole.CLIENT, true))).doesNotThrowAnyException();
        assertThatCode(() -> policy.ensureCanUseWallet(user(AccountRole.MERCHANT, false))).doesNotThrowAnyException();
    }

    @Test
    void rejectsClientWithoutApprovedKyc() {
        assertThatThrownBy(() -> policy.ensureCanUseWallet(user(AccountRole.CLIENT, false)))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("code", ErrorCodes.WALLET_KYC_REQUIRED);
    }

    @Test
    void rejectsAdmins() {
        assertThatThrownBy(() -> policy.ensureCanUseWallet(user(AccountRole.ADMIN, false)))
                .isInstanceOf(AccessDeniedException.class);
    }
}
