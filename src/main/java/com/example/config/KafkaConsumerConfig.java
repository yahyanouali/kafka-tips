package com.example.config;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Properties;

/**
 * Provides Kafka consumer properties sourced from MicroProfile configuration.
 */
@ApplicationScoped
public class KafkaConsumerConfig {

    public static final String SCHEMA_REGISTRY_URL_CONFIG = KafkaProducerConfig.SCHEMA_REGISTRY_URL_CONFIG;

    @ConfigProperty(name = "kafka.bootstrap.servers")
    String bootstrapServers;

    @ConfigProperty(name = SCHEMA_REGISTRY_URL_CONFIG)
    String schemaRegistryUrl;

    @ConfigProperty(name = "kafka.user.group.id", defaultValue = "users-consumer")
    String groupId;

    public Properties getProperties() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer");
        // Use SpecificRecord when reading Avro
        props.put("specific.avro.reader", true);
        // Allow system property override while keeping MicroProfile config as default
        props.put(SCHEMA_REGISTRY_URL_CONFIG, System.getProperty(SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl));
        return props;
    }
}
