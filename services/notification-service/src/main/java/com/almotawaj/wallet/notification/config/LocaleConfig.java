package com.almotawaj.wallet.notification.config;

import com.almotawaj.wallet.notification.config.LocaleConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@Configuration
public class LocaleConfig {
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setSupportedLocales(LocaleConstants.SUPPORTED);
        resolver.setDefaultLocale(LocaleConstants.ENGLISH);
        return resolver;
    }
}
