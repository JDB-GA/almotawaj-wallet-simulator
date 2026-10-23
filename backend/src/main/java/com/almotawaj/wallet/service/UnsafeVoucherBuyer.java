package com.almotawaj.wallet.service;

import com.almotawaj.wallet.config.constants.SimulationConstants;
import com.almotawaj.wallet.model.SimulationOutcome;
import com.almotawaj.wallet.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UnsafeVoucherBuyer {
    private final VoucherRepository voucherRepository;

    @Transactional
    public SimulationOutcome buy(UUID voucherId) throws InterruptedException {
        int seen = voucherRepository.readRemaining(voucherId);
        if (seen <= 0) {
            return SimulationOutcome.SOLD_OUT;
        }
        Thread.sleep(SimulationConstants.UNSAFE_READ_DELAY_MS);
        voucherRepository.overwriteRemaining(voucherId, seen - 1);
        return SimulationOutcome.WON;
    }
}
