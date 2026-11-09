package com.almotawaj.wallet.identity.event;

import com.almotawaj.wallet.identity.config.constants.KafkaConstants;
import com.almotawaj.wallet.identity.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountEvents {
    private final EventPublisher eventPublisher;

    public void created(User user) {
        eventPublisher.publish(KafkaConstants.ACCOUNT_EVENTS, user.getId().toString(), new AccountEvent(UUID.randomUUID(), user.getId()));
    }
}
