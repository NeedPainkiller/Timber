package xyz.needpainkiller.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Profile("kafka")
@Configuration
@Slf4j
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    String bootstrapServers;

    /**
     * Kafka Error Handler
     * - DeadLetterPublishingRecoverer : 에러가 발생한 메시지를 Dead Letter Topic 으로 전송
     * - FixedBackOff : 1초 간격으로 2번 재시도
     */
    @Bean
    public CommonErrorHandler errorHandler(KafkaOperations<Object, Object> template) {
        return new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(template), new FixedBackOff(1000L, 2));
    }

    @Bean
    public NewTopic loginAuditMessages() {
        return new NewTopic("timber__topic-audit-login", 1, (short) 1);
    }

    @Bean
    public NewTopic apiAuditMessages() {
        return new NewTopic("timber__topic-audit-api", 1, (short) 1);
    }

    @Bean
    public NewTopic fileStoredMessages() {
        return new NewTopic("timber__topic-file-stored", 1, (short) 1);
    }
}
