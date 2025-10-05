package com.example.service;

import com.example.avro.User;
import com.example.factory.KafkaConsumerFactory;
import com.example.application.port.out.UserCachePort;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Background consumer that reads User records from Kafka and persists them to Redis.
 */
@Startup
@ApplicationScoped
public class UserConsumerService {

    private static final Logger log = LoggerFactory.getLogger(UserConsumerService.class);

    private final KafkaConsumerFactory consumerFactory;
    private final UserCachePort cacheRepository;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private ExecutorService executor;

    @ConfigProperty(name = "kafka.users.topic")
    String topic;

    public UserConsumerService(KafkaConsumerFactory consumerFactory, UserCachePort cacheRepository) {
        this.consumerFactory = consumerFactory;
        this.cacheRepository = cacheRepository;
    }

    @PostConstruct
    void start() {
        executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "user-consumer-thread");
            t.setDaemon(true);
            return t;
        });
        running.set(true);
        executor.submit(this::pollLoop);
        log.info("UserConsumerService started.");
    }

    private void pollLoop() {
        var consumer = consumerFactory.getConsumer();
        try {
            consumer.subscribe(List.of(topic));
            log.info("Subscribed consumer to topic={}", topic);
            while (running.get()) {
                ConsumerRecords<String, User> records = consumer.poll(Duration.ofSeconds(1));
                if (records.isEmpty()) {
                    continue;
                }
                records.forEach(rec -> {
                    try {
                        cacheRepository.save(rec.value());
                        log.info("Consumed and cached user key={}, partition={}, offset={}", rec.key(), rec.partition(), rec.offset());
                    } catch (Exception e) {
                        log.error("Error handling consumed record key={}", rec.key(), e);
                    }
                });
            }
        } catch (WakeupException we) {
            // expected on shutdown
            log.debug("Consumer wakeup for shutdown");
        } catch (Exception e) {
            log.error("Unexpected error in consumer loop", e);
        } finally {
            try {
                consumer.unsubscribe();
            } catch (Exception ignore) {
            }
        }
    }

    @PreDestroy
    void stop() {
        running.set(false);
        try {
            consumerFactory.getConsumer().wakeup();
        } catch (Exception ignored) {
        }
        if (executor != null) {
            executor.shutdownNow();
        }
        log.info("UserConsumerService stopped.");
    }
}
