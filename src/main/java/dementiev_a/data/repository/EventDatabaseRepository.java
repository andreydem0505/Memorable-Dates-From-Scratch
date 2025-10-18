package dementiev_a.data.repository;

import dementiev_a.data.manager.PostgresManager;
import dementiev_a.data.model.Event;
import dementiev_a.data.sequence.EventSequence;
import dementiev_a.exception.NoEntityException;
import dementiev_a.io.IO;
import lombok.Getter;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class EventDatabaseRepository implements EventRepository {
    @Getter(lazy = true)
    private static final EventDatabaseRepository instance = new EventDatabaseRepository();

    private final EventSequence eventSequence = EventSequence.getInstance();
    private Statement statement;
    private PreparedStatement saveEventStatement;
    private PreparedStatement saveCelebrationToEventStatement;
    private PreparedStatement findEventByIdStatement;
    private PreparedStatement findCelebrationsIdsByEventIdStatement;
    private PreparedStatement findEventsByDateStatement;
    private PreparedStatement deleteEventStatement;
    private PreparedStatement deleteEventCelebrationsByEventIdStatement;

    private EventDatabaseRepository() {
        Connection connection = PostgresManager.getInstance().getConnection();
        try {
            statement = connection.createStatement();
            saveEventStatement = connection.prepareStatement(
                    "INSERT INTO events (id, name, description, date) VALUES (?, ?, ?, ?)"
            );
            saveCelebrationToEventStatement = connection.prepareStatement(
                    "INSERT INTO events_celebrations (celebrationId, eventId) VALUES (?, ?)"
            );
            findEventByIdStatement = connection.prepareStatement(
                    "SELECT * FROM events WHERE id = ?"
            );
            findCelebrationsIdsByEventIdStatement = connection.prepareStatement(
                    "SELECT celebrationId FROM events_celebrations WHERE eventId = ?"
            );
            findEventsByDateStatement = connection.prepareStatement(
                    "SELECT * FROM events LEFT JOIN events_celebrations ON events.id = events_celebrations.eventId" +
                            " WHERE date = ?"
            );
            deleteEventStatement = connection.prepareStatement(
                    "DELETE FROM events WHERE id = ?"
            );
            deleteEventCelebrationsByEventIdStatement = connection.prepareStatement(
                    "DELETE FROM events_celebrations WHERE eventId = ?"
            );
        } catch (SQLException e) {
            IO.printError("Error while preparing database statements");
        }
    }

    @Override
    public Set<Long> findCelebrationsIdsByEventId(Long eventId) {
        try {
            findEventByIdStatement.setLong(1, eventId);
            if (!findEventByIdStatement.executeQuery().next()) {
                throw new NoEntityException(ENTITY_NAME, String.valueOf(eventId));
            }
            findCelebrationsIdsByEventIdStatement.setLong(1, eventId);
            ResultSet celebrationsResult = findCelebrationsIdsByEventIdStatement.executeQuery();
            return extractCelebrationIds(celebrationsResult);
        } catch (SQLException e) {
            IO.printError("Error while retrieving celebration IDs from database");
            return Set.of();
        }
    }

    @Override
    public Set<Event> findByDate(LocalDate date) {
        try {
            findEventsByDateStatement.setDate(1, Date.valueOf(date));
            ResultSet result = findEventsByDateStatement.executeQuery();
            return extractEvents(result);
        } catch (SQLException e) {
            IO.printError("Error while retrieving the events from database");
            return Set.of();
        }
    }

    @Override
    public Event findById(Long id) {
        try {
            findEventByIdStatement.setLong(1, id);
            ResultSet eventResult = findEventByIdStatement.executeQuery();
            if (!eventResult.next()) {
                throw new NoEntityException(ENTITY_NAME, String.valueOf(id));
            }
            findCelebrationsIdsByEventIdStatement.setLong(1, id);
            ResultSet celebrationsResult = findCelebrationsIdsByEventIdStatement.executeQuery();
            Set<Long> celebrationsIds = extractCelebrationIds(celebrationsResult);
            return new Event(
                    id,
                    eventResult.getString("name"),
                    eventResult.getString("description"),
                    eventResult.getDate("date").toLocalDate(),
                    celebrationsIds
            );
        } catch (SQLException e) {
            IO.printError("Error while retrieving the event from database");
            return null;
        }
    }

    @Override
    public Set<Event> findAll() {
        try {
            ResultSet result = statement.executeQuery(
                    "SELECT * FROM events LEFT JOIN events_celebrations ON events.id = events_celebrations.eventId"
            );
            return extractEvents(result);
        } catch (SQLException e) {
            IO.printError("Error while retrieving events from database");
            return Set.of();
        }
    }

    @Override
    public Long save(Event entity) {
        if (entity.getId() == null) {
            entity.setId(eventSequence.next());
        }
        try {
            saveEventStatement.setLong(1, entity.getId());
            saveEventStatement.setString(2, entity.getName());
            saveEventStatement.setString(3, entity.getDescription());
            saveEventStatement.setDate(4, Date.valueOf(entity.getDate()));
            saveEventStatement.executeUpdate();
            for (long celebrationId : entity.getCelebrationIds()) {
                saveCelebrationToEventStatement.setLong(1, celebrationId);
                saveCelebrationToEventStatement.setLong(2, entity.getId());
                saveCelebrationToEventStatement.addBatch();
            }
            saveCelebrationToEventStatement.executeBatch();
        } catch (SQLException e) {
            IO.printError("Error while saving the event to database");
        }
        return entity.getId();
    }

    @Override
    public void deleteById(Long id) {
        try {
            findCelebrationsIdsByEventIdStatement.setLong(1, id);
            ResultSet celebrationsResult = findCelebrationsIdsByEventIdStatement.executeQuery();
            Set<Long> celebrationIds = extractCelebrationIds(celebrationsResult);
            deleteEventStatement.setLong(1, id);
            int affectedRows = deleteEventStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new NoEntityException(ENTITY_NAME, String.valueOf(id));
            }
            deleteEventCelebrationsByEventIdStatement.setLong(1, id);
            deleteEventCelebrationsByEventIdStatement.executeUpdate();
            statement.executeUpdate("DELETE FROM celebrations WHERE id IN (%s)"
                    .formatted(String.join(
                            ",",
                            celebrationIds.stream().map(String::valueOf).toArray(String[]::new)
                    )));
        } catch (SQLException e) {
            IO.printError("Error while deleting the event from database");
        }
    }

    @Override
    public void deleteAll() {
        try {
            statement.executeUpdate("DELETE FROM events");
            statement.executeUpdate("DELETE FROM events_celebrations");
            statement.executeUpdate("DELETE FROM celebrations");
        } catch (SQLException e) {
            IO.printError("Error while deleting events from database");
        }
    }

    private Set<Long> extractCelebrationIds(ResultSet result) throws SQLException {
        Set<Long> celebrationIds = new HashSet<>();
        while (result.next()) {
            celebrationIds.add(result.getLong("celebrationId"));
        }
        return celebrationIds;
    }

    private Set<Event> extractEvents(ResultSet result) throws SQLException {
        Map<Long, Event> map = new HashMap<>();
        while (result.next()) {
            Long eventId = result.getLong("id");
            long celebrationId = result.getLong("celebrationId");
            if (!map.containsKey(eventId)) {
                map.put(eventId, new Event(
                        eventId,
                        result.getString("name"),
                        result.getString("description"),
                        result.getDate("date").toLocalDate(),
                        new HashSet<>()
                ));
            }
            if (celebrationId != 0) {
                map.get(eventId).addCelebrationId(celebrationId);
            }
        }
        return new HashSet<>(map.values());
    }
}
