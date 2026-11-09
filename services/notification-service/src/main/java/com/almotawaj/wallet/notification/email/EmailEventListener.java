package com.almotawaj.wallet.notification.email;

import com.almotawaj.wallet.notification.config.KafkaConstants;
import com.almotawaj.wallet.notification.config.LogMessages;
import com.almotawaj.wallet.notification.email.model.EmailRequest;
import com.almotawaj.wallet.notification.email.service.CodeEmailService;
import com.almotawaj.wallet.notification.email.service.EmailService;
import com.almotawaj.wallet.notification.email.service.KycDecisionEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailEventListener {
    private final EmailService emailService;
    private final CodeEmailService codeEmailService;
    private final KycDecisionEmailService kycDecisionEmailService;
    private final JsonMapper jsonMapper;
    private final Set<UUID> sentEvents = Collections.newSetFromMap(new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<UUID, Boolean> eldest) {
            return size() > KafkaConstants.REMEMBERED_EVENTS;
        }
    });

    @KafkaListener(topics = KafkaConstants.EMAIL_EVENTS)
    public void onEmailRequested(String payload) {
        EmailRequest request = jsonMapper.readValue(payload, EmailRequest.class);
        if (sentEvents.contains(request.eventId())) {
            log.info(LogMessages.DUPLICATE_EMAIL_SKIPPED, request.eventId());
            return;
        }
        Locale locale = request.locale() == null ? Locale.ENGLISH : Locale.forLanguageTag(request.locale());
        switch (request.type()) {
            case VERIFICATION_CODE -> emailService.sendVerificationCode(request.userId(), request.email(), request.code(), locale);
            case PASSWORD_RESET -> codeEmailService.sendPasswordReset(request.userId(), request.email(), request.code(), locale);
            case INVITATION -> codeEmailService.sendInvitation(request.userId(), request.email(), request.code());
            case KYC_DECISION -> kycDecisionEmailService.send(request);
        }
        sentEvents.add(request.eventId());
        log.info(LogMessages.EMAIL_SENT, request.type(), request.userId());
    }
}
