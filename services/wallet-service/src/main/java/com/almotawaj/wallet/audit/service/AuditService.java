package com.almotawaj.wallet.audit.service;

import com.almotawaj.wallet.account.model.AccountHolder;
import com.almotawaj.wallet.account.repository.AccountHolderRepository;
import com.almotawaj.wallet.audit.model.AuditAction;
import com.almotawaj.wallet.audit.model.AuditTargetType;
import com.almotawaj.wallet.config.constants.KafkaConstants;
import com.almotawaj.wallet.event.AuditEvent;
import com.almotawaj.wallet.event.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final EventPublisher eventPublisher;
    private final AccountHolderRepository accountRepository;
    private final Clock clock;

    public void record(UUID actorId, AuditAction action, AuditTargetType targetType, UUID targetId, String details) {
        String actorEmail = actorId == null ? null
                : accountRepository.findById(actorId).map(AccountHolder::getEmailAddress).orElse(null);
        eventPublisher.publish(KafkaConstants.AUDIT_EVENTS, String.valueOf(targetId), new AuditEvent(UUID.randomUUID(),
                actorId, actorEmail, action.name(), targetType.name(), targetId, details, clock.instant()));
    }
}
