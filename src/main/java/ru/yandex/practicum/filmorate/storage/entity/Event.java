package ru.yandex.practicum.filmorate.storage.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.storage.entity.enums.EventType;
import ru.yandex.practicum.filmorate.storage.entity.enums.Operation;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    Long timestamp;
    Long userId;
    EventType eventType;
    Operation operation;
    Long eventId;
    Long entityId;

    public Event(Long timestamp, Long userId, EventType eventType, Operation operation, Long entityId) {
        this.timestamp = timestamp;
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
    }
}
