package com.almotawaj.wallet.identity.audit;

import com.almotawaj.wallet.identity.config.constants.KafkaConstants;
import com.almotawaj.wallet.identity.event.AuditEvent;
import com.almotawaj.wallet.identity.event.EventPublisher;
import com.almotawaj.wallet.identity.user.model.User;
import com.almotawaj.wallet.identity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final EventPublisher eventPublisher;
    private final UserRepository userRepository;
    private final Clock clock;

    public void record(UUID actorId, AuditAction action, AuditTargetType targetType, UUID targetId, String details) {
        String actorEmail = actorId == null ? null
                : userRepository.findById(actorId).map(User::getEmailAddress).orElse(null);
        eventPublisher.publish(KafkaConstants.AUDIT_EVENTS, String.valueOf(targetId), new AuditEvent(UUID.randomUUID(),
                actorId, actorEmail, action.name(), targetType.name(), targetId, details, clock.instant()));
    }
}
