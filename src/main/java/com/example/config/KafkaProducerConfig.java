package com.example.config;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Properties;

/**
 * Provides Kafka producer properties sourced from MicroProfile configuration.
 */
@ApplicationScoped
public class KafkaProducerConfig {

    // Config property names
    public static final String SCHEMA_REGISTRY_URL_CONFIG = "schema.registry.url";

    @ConfigProperty(name = "kafka.bootstrap.servers")
    private String bootstrapServers;

    @ConfigProperty(name = SCHEMA_REGISTRY_URL_CONFIG)
    private String schemaRegistryUrl;

    public Properties getProperties() {
        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 2);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        // Allow system property override while keeping MicroProfile config as default
        props.put(SCHEMA_REGISTRY_URL_CONFIG, System.getProperty(SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl));
        return props;
    }
}

