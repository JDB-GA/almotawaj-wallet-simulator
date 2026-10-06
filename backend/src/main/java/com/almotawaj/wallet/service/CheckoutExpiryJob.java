package com.almotawaj.wallet.service;

import com.almotawaj.wallet.config.constants.GatewayConstants;
import com.almotawaj.wallet.event.CheckoutStatusChangedEvent;
import com.almotawaj.wallet.model.AuditAction;
import com.almotawaj.wallet.model.AuditTargetType;
import com.almotawaj.wallet.model.CheckoutStatus;
import com.almotawaj.wallet.repository.CheckoutSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Component
@RequiredArgsConstructor
public class CheckoutExpiryJob {
    private final CheckoutSessionRepository sessionRepository;
    private final AuditService auditService;
    private final Clock clock;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(fixedDelay = GatewayConstants.EXPIRY_SWEEP_INTERVAL_MS)
    @Transactional
    public void expireOverdueSessions() {
        sessionRepository.findAllByStatusAndExpiresAtBefore(CheckoutStatus.PENDING, clock.instant()).forEach(session -> {
            session.setStatus(CheckoutStatus.EXPIRED);
            auditService.record(null, AuditAction.CHECKOUT_EXPIRED, AuditTargetType.CHECKOUT_SESSION, session.getId(),
                    session.getOrderReference());
            eventPublisher.publishEvent(new CheckoutStatusChangedEvent(session.getId()));
        });
    }
}
