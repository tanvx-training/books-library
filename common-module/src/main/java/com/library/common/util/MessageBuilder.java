package com.library.common.util;

import com.library.common.enums.EventType;
import com.library.common.message.KafkaMessage;
import com.library.common.message.MessageMeta;

import java.util.UUID;

public class MessageBuilder {

  public static <T> KafkaMessage<T> build(
          String serviceId, EventType eventType, String messageCode, T payload) {
    KafkaMessage<T> message = new KafkaMessage<>();
    MessageMeta meta = MessageMeta.builder()
        .messageId(generateMessageId())
        .serviceId(serviceId)
        .type(eventType)
        .timestamp(System.currentTimeMillis())
        .build();
    message.setMeta(meta);
    message.setMessageCode(messageCode);
    message.setPayload(payload);
    return message;
  }

  public static String generateMessageId() {

    return UUID.randomUUID().toString().replace("_", "");
  }
}
