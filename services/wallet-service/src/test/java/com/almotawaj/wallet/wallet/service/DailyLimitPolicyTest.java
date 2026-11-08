package com.almotawaj.wallet.wallet.service;

import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.wallet.model.TransactionType;
import com.almotawaj.wallet.wallet.repository.WalletTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailyLimitPolicyTest {
    private static final UUID WALLET_ID = UUID.randomUUID();
    private static final Instant START_OF_DAY_IN_BAHRAIN = Instant.parse("2026-10-05T21:00:00Z");

    @Mock
    private WalletTransactionRepository transactionRepository;

    private DailyLimitPolicy policy;

    @BeforeEach
    void setUp() {
        policy = new DailyLimitPolicy(transactionRepository, Clock.fixed(Instant.parse("2026-10-06T10:00:00Z"), ZoneOffset.UTC));
        when(transactionRepository.sumAmountSince(WALLET_ID, TransactionType.PAYMENT, START_OF_DAY_IN_BAHRAIN))
                .thenReturn(new BigDecimal("9990.000"));
    }

    @Test
    void checkout_allowsPayingUpToTheRestOfTheDailyLimit() {
        assertThatCode(() -> policy.ensureCheckoutAllowed(WALLET_ID, new BigDecimal("10.000"))).doesNotThrowAnyException();
    }

    @Test
    void checkout_rejectsPayingAboveTheDailyLimit() {
        assertThatThrownBy(() -> policy.ensureCheckoutAllowed(WALLET_ID, new BigDecimal("10.001")))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("code", ErrorCodes.DAILY_CHECKOUT_LIMIT_EXCEEDED);
    }
}
