package com.almotawaj.wallet.kyc.service;

import com.almotawaj.wallet.kyc.event.KycReviewedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class KycReviewNotifier {
    private final KycDecisionEmailService kycDecisionEmailService;

    @Async
    @TransactionalEventListener
    public void onKycReviewed(KycReviewedEvent event) {
        kycDecisionEmailService.send(event);
    }
}
