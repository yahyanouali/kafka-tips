package com.example.service;

import com.example.UserDto;
import com.example.avro.User;
import com.example.factory.KafkaProducerFactory;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Service responsible for publishing User events to Kafka.
 */
@ApplicationScoped
public class UserProducerService {

    private static final Logger log = LoggerFactory.getLogger(UserProducerService.class);

    private final KafkaProducerFactory producerFactory;

    @ConfigProperty(name = "kafka.user.topic")
    private String topic;

    public UserProducerService(KafkaProducerFactory factory) {
        this.producerFactory = factory;
    }

    /**
     * Publishes a user described by the API DTO. This method maps the DTO to the Avro schema
     * and delegates the actual send to Kafka.
     */
    public void publishUser(UserDto dto) {
        Objects.requireNonNull(dto, "dto must not be null");
        Objects.requireNonNull(dto.name(), "dto.name must not be null");
        final User avro = new User(dto.name(), dto.age());
        sendUser(avro);
    }

    /**
     * Sends the given user to the configured Kafka topic.
     *
     * @param user the user payload to be produced
     */
    public void sendUser(User user) {
        Objects.requireNonNull(user, "user must not be null");
        final ProducerRecord<String, User> record = new ProducerRecord<>(this.topic, user.getName(), user);
        try {
            producerFactory.getProducer().send(record, (metadata, exception) -> {
                if (exception != null) {
                    log.error("Failed to produce record to topic={} with key={}", topic, record.key(), exception);
                } else if (metadata != null) {
                    log.info("Produced record to topic={}, partition={}, offset={}, timestamp={} (key={}, headers={})",
                            metadata.topic(),
                            metadata.partition(),
                            metadata.offset(),
                            metadata.timestamp(),
                            record.key(),
                            record.headers().toArray());
                }
            });
        } catch (Exception e) {
            log.error("Unexpected error while sending Avro message to topic=" + topic, e);
        }
    }
}
