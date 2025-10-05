package com.example.factory;

import com.example.avro.User;
import com.example.config.KafkaProducerConfig;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.kafka.clients.producer.KafkaProducer;

@ApplicationScoped
public class KafkaProducerFactory {

    private final KafkaProducer<String, User> producer;

    public KafkaProducerFactory(KafkaProducerConfig config) {
         this.producer = new KafkaProducer<>(config.getProperties());
    }

    public KafkaProducer<String, User> getProducer() {
        return producer;
    }

    @PreDestroy
    public void close() {
        producer.flush();
        producer.close();
    }

}
