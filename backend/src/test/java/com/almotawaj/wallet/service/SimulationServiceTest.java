package com.almotawaj.wallet.service;

import com.almotawaj.wallet.model.SimulationMode;
import com.almotawaj.wallet.model.SimulationOutcome;
import com.almotawaj.wallet.model.request.PayoutRaceRequest;
import com.almotawaj.wallet.model.request.VoucherRaceRequest;
import com.almotawaj.wallet.model.response.PayoutRaceResult;
import com.almotawaj.wallet.model.response.VoucherRaceResult;
import com.almotawaj.wallet.repository.UserRepository;
import com.almotawaj.wallet.repository.VoucherRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SimulationServiceTest {
    @Autowired
    private SimulationService simulations;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VoucherRepository voucherRepository;

    @Test
    void safeRace_neverSellsMoreThanTheStock_andLeavesNothingBehind() throws Exception {
        long usersBefore = userRepository.count();

        VoucherRaceResult result = simulations.voucherRace(new VoucherRaceRequest(SimulationMode.SAFE, 20, 1));

        assertThat(result.winners()).isEqualTo(1);
        assertThat(result.oversold()).isZero();
        assertThat(result.remainingAfter()).isZero();
        assertThat(result.attempts()).hasSize(20).allMatch(attempt -> attempt.thread().startsWith("buyer-"));
        assertThat(result.attempts().stream().filter(attempt -> attempt.outcome() == SimulationOutcome.SOLD_OUT)).hasSize(19);
        assertThat(userRepository.count()).isEqualTo(usersBefore);
        assertThat(voucherRepository.count()).isZero();
    }

    @Test
    void unsafeRace_oversellsBecauseEveryoneReadsTheSameStock() throws Exception {
        VoucherRaceResult result = simulations.voucherRace(new VoucherRaceRequest(SimulationMode.UNSAFE, 20, 1));

        assertThat(result.winners()).isGreaterThan(1);
        assertThat(result.oversold()).isEqualTo(result.winners() - 1);
    }

    @Test
    void payoutRace_usesThePoolAndNeverOverdraws() {
        PayoutRaceResult result = simulations.payoutRace(new PayoutRaceRequest(10, new BigDecimal("15.000"), new BigDecimal("100.000")));

        assertThat(result.paid()).isEqualTo(6);
        assertThat(result.failed()).isEqualTo(4);
        assertThat(result.finalBalance()).isEqualByComparingTo("10.000");
        assertThat(result.threadsUsed()).isBetween(1, 5);
        assertThat(result.attempts()).hasSize(10).allMatch(attempt -> attempt.thread().startsWith("payout-"));
    }
}
