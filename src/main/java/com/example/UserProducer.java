package com.example;

import com.example.avro.User;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

@ApplicationScoped
public class UserProducer {

    private static final Logger log = LoggerFactory.getLogger(UserProducer.class);

    public void produce(User user) {
        try {
            Producer<String, GenericRecord> producer = createProducer();

            String topic = "users";

            ProducerRecord<String, GenericRecord> record = new ProducerRecord<>(topic, user.getName(), user);

            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    log.error("Error while producing record", exception);
                } else {
                    log.info("Record sent successfully [topic: {}, parition: {}, offset: {}, timestamp: {}] ",
                            metadata.topic(),
                            metadata.partition(),
                            metadata.offset(),
                            metadata.timestamp()
                            );
                }
            });

            producer.flush();
            producer.close();
        } catch (Exception e) {
            log.error("Error sending Avro message", e);
        }
    }

    private static Producer<String, GenericRecord> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        props.put("schema.registry.url", "http://localhost:8081");

        return new KafkaProducer<>(props);
    }

}
