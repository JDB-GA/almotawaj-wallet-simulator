package com.almotawaj.wallet.service;

import com.almotawaj.wallet.config.constants.GatewayConstants;
import com.almotawaj.wallet.event.CheckoutStatusChangedEvent;
import com.almotawaj.wallet.model.CheckoutSession;
import com.almotawaj.wallet.model.MerchantWebhook;
import com.almotawaj.wallet.model.WebhookPayload;
import com.almotawaj.wallet.repository.CheckoutSessionRepository;
import com.almotawaj.wallet.repository.MerchantWebhookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Locale;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CheckoutWebhookListener {
    private final CheckoutSessionRepository sessionRepository;
    private final MerchantWebhookRepository webhookRepository;
    private final WebhookSender sender;

    @Async
    @TransactionalEventListener
    public void onStatusChanged(CheckoutStatusChangedEvent event) throws InterruptedException {
        Optional<CheckoutSession> found = sessionRepository.findWithMerchantById(event.sessionId());
        if (found.isEmpty()) {
            return;
        }
        CheckoutSession session = found.get();
        Optional<MerchantWebhook> webhook = webhookRepository.findByMerchantId(session.getMerchant().getId());
        if (webhook.isPresent()) {
            sender.send(webhook.get(), toPayload(session));
        }
    }

    private static WebhookPayload toPayload(CheckoutSession session) {
        String event = GatewayConstants.WEBHOOK_EVENT_PREFIX + session.getStatus().name().toLowerCase(Locale.ROOT);
        return new WebhookPayload(event, session.getId(), session.getOrderReference(), session.getAmount(), session.getStatus(),
                session.getUpdatedAt());
    }
}
