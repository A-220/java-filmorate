package ru.yandex.practicum.filmorate.storage.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    Long timestamp;
    Long userId;
    String eventType;
    String operation;
    Long eventId;
    Long entityId;

    public Event(Long timestamp, Long userId, String eventType, String operation, Long entityId) {
        this.timestamp = timestamp;
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
    }
}
