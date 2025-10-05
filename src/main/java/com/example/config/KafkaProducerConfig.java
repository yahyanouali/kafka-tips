package com.example.config;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Properties;

@ApplicationScoped
public class KafkaProducerConfig {

    @ConfigProperty(name = "kafka.bootstrap.servers")
    String bootstrapServers;


    public Properties getProperties() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 2);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        props.put("schema.registry.url", System.getProperty("schema.registry.url", "http://localhost:8081"));
        return props;
    }
}

