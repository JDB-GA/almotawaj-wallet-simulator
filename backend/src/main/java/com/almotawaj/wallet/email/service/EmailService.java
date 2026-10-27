package com.almotawaj.wallet.email.service;

import com.almotawaj.wallet.config.constants.LocaleConstants;
import com.almotawaj.wallet.config.constants.MailConstants;
import com.almotawaj.wallet.config.constants.OtpConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.UUID;

@Service
public class EmailService {
    private final EmailSender emailSender;
    private final MessageSource messageSource;
    private final ITemplateEngine templateEngine;
    private final String logoUrl;

    public EmailService(EmailSender emailSender, MessageSource messageSource, ITemplateEngine templateEngine,
                        @Value(MailConstants.LOGO_URL_PROPERTY) String logoUrl) {
        this.emailSender = emailSender;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
        this.logoUrl = logoUrl;
    }

    @Async
    public void sendVerificationCode(UUID userId, String to, String code, Locale locale) {
        emailSender.send(userId, to, messageSource.getMessage(MailConstants.VERIFICATION_SUBJECT_KEY, null, locale),
                renderPlainText(code, locale), renderHtml(code, locale));
    }

    private String renderPlainText(String code, Locale locale) {
        return messageSource.getMessage(MailConstants.VERIFICATION_BODY_KEY,
                new Object[]{code, OtpConstants.EXPIRY.toMinutes()}, locale);
    }

    private String renderHtml(String code, Locale locale) {
        Context context = new Context(locale);
        context.setVariable(MailConstants.CODE_VARIABLE, code);
        context.setVariable(MailConstants.EXPIRY_MINUTES_VARIABLE, OtpConstants.EXPIRY.toMinutes());
        context.setVariable(MailConstants.LOGO_URL_VARIABLE, logoUrl);
        context.setVariable(MailConstants.DIRECTION_VARIABLE, isRightToLeft(locale)
                ? MailConstants.DIRECTION_RTL : MailConstants.DIRECTION_LTR);
        return templateEngine.process(MailConstants.VERIFICATION_TEMPLATE, context);
    }

    private static boolean isRightToLeft(Locale locale) {
        return LocaleConstants.ARABIC.getLanguage().equals(locale.getLanguage());
    }
}
