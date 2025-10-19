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

    private final EventSequence sequence = EventSequence.getInstance();
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
            try (ResultSet result = statement.executeQuery("SELECT MAX(id) FROM events")) {
                if (result.next()) {
                    long maxId = result.getLong(1);
                    sequence.setValue(maxId + 1);
                }
            }
            saveEventStatement = connection.prepareStatement(
                    "INSERT INTO events (id, name, description, date) VALUES (?, ?, ?, ?) ON CONFLICT (id) DO UPDATE SET " +
                            "name = EXCLUDED.name, description = EXCLUDED.description, date = EXCLUDED.date"
            );
            saveCelebrationToEventStatement = connection.prepareStatement(
                    "INSERT INTO events_celebrations (celebrationId, eventId) VALUES (?, ?) " +
                            "ON CONFLICT (celebrationId) DO NOTHING"
            );
            findEventByIdStatement = connection.prepareStatement(
                    "SELECT * FROM events LEFT JOIN events_celebrations ON events.id = events_celebrations.eventId " +
                            "WHERE id = ?"
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
            try (ResultSet celebrationsResult = findCelebrationsIdsByEventIdStatement.executeQuery()) {
                return extractCelebrationIds(celebrationsResult);
            }
        } catch (SQLException e) {
            IO.printError("Error while retrieving celebration IDs from database");
            return Set.of();
        }
    }

    @Override
    public List<Event> findByDate(LocalDate date) {
        try {
            findEventsByDateStatement.setDate(1, Date.valueOf(date));
            try (ResultSet result = findEventsByDateStatement.executeQuery()) {
                return extractEvents(result);
            }
        } catch (SQLException e) {
            IO.printError("Error while retrieving the events from database");
            return List.of();
        }
    }

    @Override
    public Event findById(Long id) {
        try {
            findEventByIdStatement.setLong(1, id);
            try (ResultSet result = findEventByIdStatement.executeQuery()) {
                List<Event> events = extractEvents(result);
                if (events.isEmpty()) {
                    throw new NoEntityException(ENTITY_NAME, String.valueOf(id));
                }
                return events.iterator().next();
            }
        } catch (SQLException e) {
            IO.printError("Error while retrieving the event from database");
            return null;
        }
    }

    @Override
    public List<Event> findAll() {
        try (ResultSet result = statement.executeQuery(
                "SELECT * FROM events LEFT JOIN events_celebrations ON events.id = events_celebrations.eventId"
        )) {
            return extractEvents(result);
        } catch (SQLException e) {
            IO.printError("Error while retrieving events from database");
            return List.of();
        }
    }

    @Override
    public Long save(Event entity) {
        if (entity.getId() == null) {
            entity.setId(sequence.next());
        }
        try {
            saveEventStatement.setLong(1, entity.getId());
            saveEventStatement.setString(2, entity.getName());
            saveEventStatement.setString(3, entity.getDescription());
            saveEventStatement.setDate(4, Date.valueOf(entity.getDate()));
            saveEventStatement.executeUpdate();
            deleteEventCelebrationsByEventIdStatement.setLong(1, entity.getId());
            deleteEventCelebrationsByEventIdStatement.executeUpdate();
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
            deleteEventStatement.setLong(1, id);
            int affectedRows = deleteEventStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new NoEntityException(ENTITY_NAME, String.valueOf(id));
            }
            deleteEventCelebrationsByEventIdStatement.setLong(1, id);
            deleteEventCelebrationsByEventIdStatement.executeUpdate();
        } catch (SQLException e) {
            IO.printError("Error while deleting the event from database");
        }
    }

    @Override
    public void deleteAll() {
        try {
            statement.executeUpdate("DELETE FROM events");
            statement.executeUpdate("DELETE FROM events_celebrations");
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

    private List<Event> extractEvents(ResultSet result) throws SQLException {
        Map<Long, Event> map = new TreeMap<>();
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
        return new ArrayList<>(map.values());
    }
}
