package com.almotawaj.wallet.email.service;

import com.almotawaj.wallet.config.constants.LocaleConstants;
import com.almotawaj.wallet.config.constants.MailConstants;
import com.almotawaj.wallet.config.constants.OtpConstants;
import com.almotawaj.wallet.email.model.CodeEmailSection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CodeEmailService {
    private final EmailSender emailSender;
    private final MessageSource messageSource;
    private final ITemplateEngine templateEngine;
    private final String logoUrl;
    private final String resetUrl;

    public CodeEmailService(EmailSender emailSender, MessageSource messageSource, ITemplateEngine templateEngine,
                            @Value(MailConstants.LOGO_URL_PROPERTY) String logoUrl,
                            @Value(MailConstants.APP_URL_PROPERTY) String appUrl) {
        this.emailSender = emailSender;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
        this.logoUrl = logoUrl;
        this.resetUrl = appUrl + MailConstants.RESET_PASSWORD_PATH;
    }

    @Async
    public void sendPasswordReset(UUID userId, String to, String code, Locale locale) {
        send(userId, to, code, MailConstants.PASSWORD_RESET_KEY_PREFIX, OtpConstants.EXPIRY.toMinutes(), List.of(locale), locale);
    }

    @Async
    public void sendInvitation(UUID userId, String to, String code) {
        send(userId, to, code, MailConstants.INVITATION_KEY_PREFIX, OtpConstants.INVITATION_EXPIRY.toHours(),
                LocaleConstants.SUPPORTED, LocaleConstants.ENGLISH);
    }

    private void send(UUID userId, String to, String code, String keyPrefix, long expiry, List<Locale> locales, Locale subjectLocale) {
        String subject = messageSource.getMessage(keyPrefix + MailConstants.SUBJECT_SUFFIX, null, subjectLocale);
        List<CodeEmailSection> sections = locales.stream().map(locale -> section(keyPrefix, expiry, locale)).toList();
        emailSender.send(userId, to, subject, renderText(code, sections), renderHtml(subject, code, sections));
    }

    private CodeEmailSection section(String keyPrefix, long expiry, Locale locale) {
        boolean rtl = LocaleConstants.ARABIC.getLanguage().equals(locale.getLanguage());
        return new CodeEmailSection(
                locale.getLanguage(),
                rtl ? MailConstants.DIRECTION_RTL : MailConstants.DIRECTION_LTR,
                messageSource.getMessage(keyPrefix + MailConstants.HEADING_SUFFIX, null, locale),
                messageSource.getMessage(keyPrefix + MailConstants.INTRO_SUFFIX, null, locale),
                messageSource.getMessage(keyPrefix + MailConstants.EXPIRY_SUFFIX, new Object[]{expiry}, locale),
                messageSource.getMessage(keyPrefix + MailConstants.IGNORE_SUFFIX, null, locale),
                messageSource.getMessage(MailConstants.CODE_ACTION_KEY, null, locale));
    }

    private String renderHtml(String subject, String code, List<CodeEmailSection> sections) {
        Context context = new Context(LocaleConstants.ENGLISH);
        context.setVariable(MailConstants.SUBJECT_VARIABLE, subject);
        context.setVariable(MailConstants.CODE_VARIABLE, code);
        context.setVariable(MailConstants.SECTIONS_VARIABLE, sections);
        context.setVariable(MailConstants.LOGO_URL_VARIABLE, logoUrl);
        context.setVariable(MailConstants.ACTION_URL_VARIABLE, resetUrl);
        return templateEngine.process(MailConstants.CODE_NOTICE_TEMPLATE, context);
    }

    private String renderText(String code, List<CodeEmailSection> sections) {
        return sections.stream()
                .map(section -> String.join(MailConstants.PARAGRAPH_SEPARATOR, section.heading(),
                        section.intro() + " " + code, section.expiry(), section.ignore(), resetUrl))
                .collect(Collectors.joining(MailConstants.TEXT_SECTION_SEPARATOR));
    }
}
