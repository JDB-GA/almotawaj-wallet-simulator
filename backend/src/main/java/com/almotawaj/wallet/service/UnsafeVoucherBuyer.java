package com.almotawaj.wallet.service;

import com.almotawaj.wallet.config.constants.SimulationConstants;
import com.almotawaj.wallet.model.SimulationOutcome;
import com.almotawaj.wallet.model.SimulationStepType;
import com.almotawaj.wallet.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UnsafeVoucherBuyer {
    private final VoucherRepository voucherRepository;

    @Transactional
    public SimulationOutcome buy(UUID voucherId) throws InterruptedException {
        int seen = voucherRepository.readRemaining(voucherId);
        SimulationTrace.mark(SimulationStepType.READ, BigDecimal.valueOf(seen));
        if (seen <= 0) {
            SimulationTrace.mark(SimulationStepType.SOLD_OUT);
            return SimulationOutcome.SOLD_OUT;
        }
        SimulationTrace.mark(SimulationStepType.CHECK, BigDecimal.valueOf(seen));
        Thread.sleep(SimulationConstants.UNSAFE_READ_DELAY_MS);
        SimulationTrace.mark(SimulationStepType.SLEEP);
        voucherRepository.overwriteRemaining(voucherId, seen - 1);
        SimulationTrace.mark(SimulationStepType.WRITE, BigDecimal.valueOf(seen - 1));
        return SimulationOutcome.WON;
    }
}
