package com.example.factory;

import com.example.avro.User;
import com.example.config.KafkaConsumerConfig;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory responsible for creating and managing the lifecycle of a KafkaConsumer.
 */
@ApplicationScoped
public class KafkaConsumerFactory {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerFactory.class);

    private final KafkaConsumer<String, User> consumer;

    public KafkaConsumerFactory(KafkaConsumerConfig config) {
        this.consumer = new KafkaConsumer<>(config.getProperties());
    }

    public KafkaConsumer<String, User> getConsumer() {
        return consumer;
    }

    @PreDestroy
    public void close() {
        try {
            consumer.wakeup();
            consumer.close();
            log.info("Kafka consumer closed successfully");
        } catch (Exception e) {
            log.warn("Error while closing Kafka consumer", e);
        }
    }
}
