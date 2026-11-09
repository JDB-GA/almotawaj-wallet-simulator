package com.almotawaj.wallet.notification.email;

import com.almotawaj.wallet.notification.email.service.CodeEmailService;
import com.almotawaj.wallet.notification.email.service.EmailService;
import com.almotawaj.wallet.notification.email.service.KycDecisionEmailService;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class EmailEventListenerTest {
    private final EmailService emailService = mock(EmailService.class);
    private final CodeEmailService codeEmailService = mock(CodeEmailService.class);
    private final KycDecisionEmailService kycDecisionEmailService = mock(KycDecisionEmailService.class);
    private final EmailEventListener listener = new EmailEventListener(emailService, codeEmailService,
            kycDecisionEmailService, JsonMapper.builder().build());
    private final UUID userId = UUID.randomUUID();

    private String verificationEvent(UUID eventId) {
        return "{\"eventId\":\"" + eventId + "\",\"type\":\"VERIFICATION_CODE\",\"userId\":\"" + userId
                + "\",\"email\":\"a@b.com\",\"locale\":\"ar\",\"code\":\"123456\"}";
    }

    @Test
    void verificationCode_isSentInTheUsersLanguage() {
        listener.onEmailRequested(verificationEvent(UUID.randomUUID()));

        verify(emailService).sendVerificationCode(userId, "a@b.com", "123456", Locale.forLanguageTag("ar"));
    }

    @Test
    void duplicateEvent_isSentOnlyOnce() {
        String event = verificationEvent(UUID.randomUUID());

        listener.onEmailRequested(event);
        listener.onEmailRequested(event);

        verify(emailService, times(1)).sendVerificationCode(any(), any(), any(), any());
    }

    @Test
    void failedSend_isNotRememberedSoTheRetryWorks() {
        String event = verificationEvent(UUID.randomUUID());
        doThrow(new IllegalStateException("down")).doNothing()
                .when(emailService).sendVerificationCode(any(), any(), any(), any());

        assertThatThrownBy(() -> listener.onEmailRequested(event)).isInstanceOf(IllegalStateException.class);
        listener.onEmailRequested(event);

        verify(emailService, times(2)).sendVerificationCode(any(), any(), any(), any());
    }
}
