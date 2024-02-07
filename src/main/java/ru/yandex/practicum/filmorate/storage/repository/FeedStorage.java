package ru.yandex.practicum.filmorate.storage.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.entity.Event;
import ru.yandex.practicum.filmorate.storage.entity.enums.EventType;
import ru.yandex.practicum.filmorate.storage.entity.enums.Operation;

import java.util.ArrayList;
import java.util.List;

@Component
public class FeedStorage {

    private final JdbcTemplate jdbcTemplate;

    public FeedStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Event saveToFeed(Event event) {
        String sql = "INSERT INTO feed(timestamp, user_id, event_type, operation, entity_id)" +
                "VALUES(?, ?, ?, ?, ?) ";

        jdbcTemplate.update(sql, event.getTimestamp(), event.getUserId(), event.getEventType().name(), event.getOperation().name(), event.getEntityId());

        return event;
    }

    public List<Event> getUserFeed(Long id) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT * FROM feed WHERE user_id = ? ORDER BY event_id", id);

        List<Event> feed = new ArrayList<>();
        while (rs.next()) {
            Event event = Event.builder()
                    .timestamp(rs.getLong("timestamp"))
                    .userId(rs.getLong("user_id"))
                    .eventType(EventType.valueOf(rs.getString("event_type")))
                    .operation(Operation.valueOf(rs.getString("operation")))
                    .eventId(rs.getLong("event_id"))
                    .entityId(rs.getLong("entity_id"))
                    .build();

            feed.add(event);
        }
        return feed;
    }
}
