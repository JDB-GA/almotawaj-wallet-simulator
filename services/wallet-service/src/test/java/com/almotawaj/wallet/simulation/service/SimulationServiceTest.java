package com.almotawaj.wallet.simulation.service;

import com.almotawaj.wallet.simulation.model.SimulationMode;
import com.almotawaj.wallet.simulation.model.SimulationOutcome;
import com.almotawaj.wallet.simulation.model.SimulationStepType;
import com.almotawaj.wallet.simulation.model.request.PayoutRaceRequest;
import com.almotawaj.wallet.simulation.model.request.VoucherRaceRequest;
import com.almotawaj.wallet.simulation.model.response.PayoutRaceResult;
import com.almotawaj.wallet.simulation.model.response.SimulationAttempt;
import com.almotawaj.wallet.simulation.model.response.SimulationStep;
import com.almotawaj.wallet.simulation.model.response.VoucherRaceResult;
import com.almotawaj.wallet.user.repository.UserRepository;
import com.almotawaj.wallet.voucher.repository.VoucherRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

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
        assertThat(result.attempts()).allMatch(attempt -> attempt.steps().get(0).type() == SimulationStepType.READ);
        assertThat(result.attempts().stream().flatMap(attempt -> attempt.steps().stream())
                .filter(step -> step.type() == SimulationStepType.TAKE_ONE && step.value().intValue() == 1)).hasSize(1);
    }

    @Test
    void unsafeRace_oversellsBecauseEveryoneReadsTheSameStock() throws Exception {
        VoucherRaceResult result = simulations.voucherRace(new VoucherRaceRequest(SimulationMode.UNSAFE, 20, 1));

        assertThat(result.winners()).isGreaterThan(1);
        assertThat(result.oversold()).isEqualTo(result.winners() - 1);
        List<SimulationAttempt> winners = result.attempts().stream()
                .filter(attempt -> attempt.outcome() == SimulationOutcome.WON).toList();
        assertThat(winners).allSatisfy(attempt -> assertThat(attempt.steps()).extracting(SimulationStep::type)
                .containsExactly(SimulationStepType.READ, SimulationStepType.CHECK, SimulationStepType.SLEEP, SimulationStepType.WRITE));
        assertThat(winners).allMatch(attempt -> attempt.steps().get(0).value().intValue() == 1
                && attempt.steps().get(3).value().intValue() == 0);
    }

    @Test
    void payoutRace_usesThePoolAndNeverOverdraws() {
        PayoutRaceResult result = simulations.payoutRace(new PayoutRaceRequest(10, new BigDecimal("15.000"), new BigDecimal("100.000")));

        assertThat(result.paid()).isEqualTo(6);
        assertThat(result.failed()).isEqualTo(4);
        assertThat(result.finalBalance()).isEqualByComparingTo("10.000");
        assertThat(result.threadsUsed()).isBetween(1, 5);
        assertThat(result.attempts()).hasSize(10).allMatch(attempt -> attempt.thread().startsWith("payout-"));
        assertThat(result.attempts()).allMatch(attempt -> attempt.steps().get(attempt.steps().size() - 1).type()
                == (attempt.outcome() == SimulationOutcome.PAID ? SimulationStepType.PAID : SimulationStepType.FAILED));
        assertThat(result.attempts()).allMatch(attempt -> attempt.steps().get(0).atMs() >= attempt.startMs());
    }
}
