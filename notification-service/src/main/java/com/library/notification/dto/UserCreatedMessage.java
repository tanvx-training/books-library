package com.library.notification.dto;

import com.library.common.event.UserCreatedEvent;
import com.library.common.model.KafkaEvent;

public class UserCreatedMessage extends KafkaEvent<UserCreatedEvent> {
}
