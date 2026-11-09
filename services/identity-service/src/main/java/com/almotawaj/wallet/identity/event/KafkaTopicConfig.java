package com.almotawaj.wallet.identity.event;

import com.almotawaj.wallet.identity.config.constants.KafkaConstants;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    private static final int PARTITIONS = 3;

    @Bean
    public NewTopic accountEventsTopic() {
        return TopicBuilder.name(KafkaConstants.ACCOUNT_EVENTS).partitions(PARTITIONS).build();
    }

    @Bean
    public NewTopic emailEventsTopic() {
        return TopicBuilder.name(KafkaConstants.EMAIL_EVENTS).partitions(PARTITIONS).build();
    }

    @Bean
    public NewTopic auditEventsTopic() {
        return TopicBuilder.name(KafkaConstants.AUDIT_EVENTS).partitions(PARTITIONS).build();
    }
}
