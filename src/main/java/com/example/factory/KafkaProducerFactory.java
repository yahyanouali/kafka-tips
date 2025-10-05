package com.example.factory;

import com.example.avro.User;
import com.example.config.KafkaProducerConfig;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory responsible for creating and managing the lifecycle of a KafkaProducer.
 */
@ApplicationScoped
public class KafkaProducerFactory {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerFactory.class);

    private final KafkaProducer<String, User> producer;

    public KafkaProducerFactory(KafkaProducerConfig config) {
         this.producer = new KafkaProducer<>(config.getProperties());
    }

    public KafkaProducer<String, User> getProducer() {
        return producer;
    }

    @PreDestroy
    public void close() {
        try {
            producer.flush();
            producer.close();
            log.info("Kafka producer closed successfully");
        } catch (Exception e) {
            log.warn("Error while closing Kafka producer", e);
        }
    }

}
