package com.almotawaj.wallet.config.app;

import com.almotawaj.wallet.config.constants.MailConstants;
import com.resend.Resend;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResendConfig {
    @Bean
    public Resend resend(@Value(MailConstants.API_KEY_PROPERTY) String apiKey) {
        return new Resend(apiKey);
    }
}
