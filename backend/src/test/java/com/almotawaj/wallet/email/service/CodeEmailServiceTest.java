package com.almotawaj.wallet.email.service;

import com.almotawaj.wallet.config.constants.LocaleConstants;
import com.almotawaj.wallet.config.constants.MailConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.thymeleaf.ITemplateEngine;

import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CodeEmailServiceTest {
    @Mock
    private EmailSender emailSender;
    @Mock
    private MessageSource messageSource;
    @Mock
    private ITemplateEngine templateEngine;

    private CodeEmailService service;

    @BeforeEach
    void setUp() {
        service = new CodeEmailService(emailSender, messageSource, templateEngine, "https://logo.url", "https://app.url");
    }

    @Test
    void sendPasswordReset_compilesAndSendsEmail() {
        UUID userId = UUID.randomUUID();
        when(messageSource.getMessage(eq(MailConstants.PASSWORD_RESET_KEY_PREFIX + MailConstants.SUBJECT_SUFFIX), any(), any()))
                .thenReturn("Reset Password");
        when(messageSource.getMessage(eq(MailConstants.PASSWORD_RESET_KEY_PREFIX + MailConstants.HEADING_SUFFIX), any(), any()))
                .thenReturn("Heading");
        when(messageSource.getMessage(eq(MailConstants.PASSWORD_RESET_KEY_PREFIX + MailConstants.INTRO_SUFFIX), any(), any()))
                .thenReturn("Intro");
        when(messageSource.getMessage(eq(MailConstants.PASSWORD_RESET_KEY_PREFIX + MailConstants.EXPIRY_SUFFIX), any(), any()))
                .thenReturn("Expiry");
        when(messageSource.getMessage(eq(MailConstants.PASSWORD_RESET_KEY_PREFIX + MailConstants.IGNORE_SUFFIX), any(), any()))
                .thenReturn("Ignore");
        when(messageSource.getMessage(eq(MailConstants.CODE_ACTION_KEY), any(), any()))
                .thenReturn("Action");

        when(templateEngine.process(eq(MailConstants.CODE_NOTICE_TEMPLATE), any())).thenReturn("<html>HTML Content</html>");

        service.sendPasswordReset(userId, "test@example.com", "123456", LocaleConstants.ENGLISH);

        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> htmlCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailSender).send(eq(userId), eq("test@example.com"), eq("Reset Password"), textCaptor.capture(), htmlCaptor.capture());

        assertThat(htmlCaptor.getValue()).isEqualTo("<html>HTML Content</html>");
        assertThat(textCaptor.getValue()).contains("123456").contains("https://app.url/reset-password");
    }
}
