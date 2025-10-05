package com.example.application.port.out;

import com.example.avro.User;

/**
 * Outbound port for publishing user events.
 * Implemented by infrastructure adapters (e.g., Kafka producer).
 */
public interface UserEventPublisherPort {
    void sendUser(User user);
}
