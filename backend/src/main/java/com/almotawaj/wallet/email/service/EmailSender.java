package com.almotawaj.wallet.email.service;

import com.almotawaj.wallet.config.constants.LogMessages;
import com.almotawaj.wallet.config.constants.MailConstants;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class EmailSender {
    private final Resend resend;
    private final String fromAddress;

    public EmailSender(Resend resend, @Value(MailConstants.FROM_PROPERTY) String fromAddress) {
        this.resend = resend;
        this.fromAddress = fromAddress;
    }

    public void send(UUID userId, String to, String subject, String text, String html) {
        try {
            resend.emails().send(CreateEmailOptions.builder()
                    .from(fromAddress)
                    .to(to)
                    .subject(subject)
                    .text(text)
                    .html(html)
                    .build());
        } catch (ResendException e) {
            log.error(LogMessages.EMAIL_SEND_FAILED, userId, e);
        }
    }
}
