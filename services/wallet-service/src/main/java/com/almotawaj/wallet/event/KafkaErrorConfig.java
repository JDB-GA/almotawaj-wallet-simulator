package com.almotawaj.wallet.event;

import com.almotawaj.wallet.config.constants.KafkaConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorConfig {
    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<?, ?> kafkaTemplate) {
        return new DefaultErrorHandler(new DeadLetterPublishingRecoverer(kafkaTemplate),
                new FixedBackOff(KafkaConstants.RETRY_INTERVAL_MS, KafkaConstants.MAX_RETRIES));
    }
}
