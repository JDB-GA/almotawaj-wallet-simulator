package com.almotawaj.wallet.kyc.service;

import com.almotawaj.wallet.config.constants.LocaleConstants;
import com.almotawaj.wallet.config.constants.MailConstants;
import com.almotawaj.wallet.email.model.EmailSection;
import com.almotawaj.wallet.email.service.EmailSender;
import com.almotawaj.wallet.kyc.event.KycReviewedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class KycDecisionEmailService {
    private final EmailSender emailSender;
    private final MessageSource messageSource;
    private final ITemplateEngine templateEngine;
    private final String logoUrl;

    public KycDecisionEmailService(EmailSender emailSender, MessageSource messageSource, ITemplateEngine templateEngine,
                                   @Value(MailConstants.LOGO_URL_PROPERTY) String logoUrl) {
        this.emailSender = emailSender;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
        this.logoUrl = logoUrl;
    }

    public void send(KycReviewedEvent event) {
        String subject = messageSource.getMessage(MailConstants.KYC_SUBJECT_KEY, null, LocaleConstants.ENGLISH);
        List<EmailSection> sections = LocaleConstants.SUPPORTED.stream()
                .map(locale -> section(event, locale))
                .toList();
        emailSender.send(event.userId(), event.email(), subject, renderText(sections, event.reason()),
                renderHtml(subject, sections, event.reason()));
    }

    private EmailSection section(KycReviewedEvent event, Locale locale) {
        String key = MailConstants.KYC_KEY_PREFIX + event.status().name();
        return new EmailSection(
                locale.getLanguage(),
                LocaleConstants.ARABIC.getLanguage().equals(locale.getLanguage())
                        ? MailConstants.DIRECTION_RTL : MailConstants.DIRECTION_LTR,
                messageSource.getMessage(key + MailConstants.HEADING_SUFFIX, null, locale),
                messageSource.getMessage(key + MailConstants.KYC_BODY_SUFFIX, null, locale),
                messageSource.getMessage(MailConstants.KYC_REASON_LABEL_KEY, null, locale));
    }

    private String renderHtml(String subject, List<EmailSection> sections, String reason) {
        Context context = new Context(LocaleConstants.ENGLISH);
        context.setVariable(MailConstants.SUBJECT_VARIABLE, subject);
        context.setVariable(MailConstants.SECTIONS_VARIABLE, sections);
        context.setVariable(MailConstants.REASON_VARIABLE, reason);
        context.setVariable(MailConstants.LOGO_URL_VARIABLE, logoUrl);
        return templateEngine.process(MailConstants.KYC_DECISION_TEMPLATE, context);
    }

    private static String renderText(List<EmailSection> sections, String reason) {
        return sections.stream()
                .map(section -> String.join(MailConstants.PARAGRAPH_SEPARATOR, section.heading(), section.body(),
                        reason == null ? "" : section.reasonLabel() + " " + reason).strip())
                .collect(Collectors.joining(MailConstants.TEXT_SECTION_SEPARATOR));
    }
}
