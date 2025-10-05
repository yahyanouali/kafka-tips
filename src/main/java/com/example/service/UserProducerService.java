package com.example.service;

import com.example.avro.User;
import com.example.factory.KafkaProducerFactory;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class UserProducerService {

    private static final Logger log = LoggerFactory.getLogger(UserProducerService.class);
    private final KafkaProducerFactory producerFactory;

    @ConfigProperty(name = "kafka.user.topic")
    String topic;

    public UserProducerService(KafkaProducerFactory factory) {
        this.producerFactory = factory;
    }

    public void sendUser(User user) {
        ProducerRecord<String, User> record = new ProducerRecord<>(this.topic, user.getName(), user);
        try {
            producerFactory.getProducer().send(record, (metadata, exception) -> {
                if (exception != null) {
                    log.error("Error while producing record", exception);
                } else {
                    log.info("Record with key={}, value={} and headers={} sent successfully to [topic: {}, partition: {}, offset: {}, timestamp: {}]",
                            record.key(),
                            record.value(),
                            record.headers().toArray(),
                            metadata.topic(),
                            metadata.partition(),
                            metadata.offset(),
                            metadata.timestamp());
                }
            });
        } catch (Exception e) {
            log.error("Error sending Avro message", e);
        }
    }
}
