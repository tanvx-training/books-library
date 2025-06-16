package com.library.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaEvent<T> {

    private String eventId;
    private String eventType;
    private String source;
    private Instant timestamp;
    private T payload;

    public static <T> KafkaEvent<T> create(String eventType, String source, T payload) {
        return KafkaEvent.<T>builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .source(source)
                .timestamp(Instant.now())
                .payload(payload)
                .build();
    }
}
