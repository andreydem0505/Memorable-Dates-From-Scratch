package dementiev_a.data.repository;

import dementiev_a.data.manager.PostgresManager;
import dementiev_a.data.model.Celebration;
import dementiev_a.data.sequence.CelebrationSequence;
import dementiev_a.exception.NoEntityException;
import dementiev_a.io.IO;
import lombok.Getter;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class CelebrationDatabaseRepository implements CelebrationRepository {
    @Getter(lazy = true)
    private static final CelebrationDatabaseRepository instance = new CelebrationDatabaseRepository();

    private final CelebrationSequence sequence = CelebrationSequence.getInstance();
    private Statement statement;
    private PreparedStatement saveCelebrationStatement;
    private PreparedStatement saveEventCelebrationStatement;
    private PreparedStatement findCelebrationByIdStatement;
    private PreparedStatement deleteCelebrationByIdStatement;
    private PreparedStatement deleteEventCelebrationByIdStatement;

    private CelebrationDatabaseRepository() {
        Connection connection = PostgresManager.getInstance().getConnection();
        try {
            statement = connection.createStatement();
            try (ResultSet result = statement.executeQuery("SELECT MAX(id) FROM celebrations")) {
                if (result.next()) {
                    long maxId = result.getLong(1);
                    sequence.setValue(maxId + 1);
                }
            }
            saveCelebrationStatement = connection.prepareStatement(
                    "INSERT INTO celebrations (id, name, description, date, place) VALUES (?, ?, ?, ?, ?) " +
                            "ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, " +
                            "description = EXCLUDED.description, date = EXCLUDED.date, place = EXCLUDED.place"
            );
            saveEventCelebrationStatement = connection.prepareStatement(
                    "INSERT INTO events_celebrations (celebrationId, eventId) VALUES (?, ?) " +
                            "ON CONFLICT (celebrationId) DO UPDATE SET eventId = EXCLUDED.eventId"
            );
            findCelebrationByIdStatement = connection.prepareStatement(
                    "SELECT * FROM celebrations LEFT JOIN events_celebrations " +
                            "ON celebrations.id = events_celebrations.celebrationId WHERE id = ?"
            );
            deleteCelebrationByIdStatement = connection.prepareStatement(
                    "DELETE FROM celebrations WHERE id = ?"
            );
            deleteEventCelebrationByIdStatement = connection.prepareStatement(
                    "DELETE FROM events_celebrations WHERE celebrationId = ?"
            );
        } catch (SQLException e) {
            IO.printError("Error while preparing database statements");
        }
    }

    @Override
    public List<Celebration> findAllByIds(Collection<Long> ids) {
        String dividedIds = String.join(",", ids.stream().map(String::valueOf).toArray(String[]::new));
        if (dividedIds.isEmpty()) {
            return List.of();
        }
        try (ResultSet result = statement.executeQuery(
                ("SELECT * FROM celebrations LEFT JOIN events_celebrations " +
                        "ON celebrations.id = events_celebrations.celebrationId WHERE id IN (%s)")
                        .formatted(dividedIds)
        )) {
            return extractCelebrations(result);
        } catch (SQLException e) {
            IO.printError("Error while retrieving the celebrations from database");
            return List.of();
        }
    }

    @Override
    public void deleteAllByIds(Collection<Long> ids) {
        String dividedIds = String.join(",", ids.stream().map(String::valueOf).toArray(String[]::new));
        try {
            statement.executeUpdate("DELETE FROM celebrations WHERE id IN (%s)".formatted(dividedIds));
            statement.executeUpdate("DELETE FROM events_celebrations WHERE celebrationId IN (%s)".formatted(dividedIds));
        } catch (SQLException e) {
            IO.printError("Error while deleting the celebrations from database");
        }
    }

    @Override
    public Celebration findById(Long id) {
        try {
            findCelebrationByIdStatement.setLong(1, id);
            try (ResultSet result = findCelebrationByIdStatement.executeQuery()) {
                List<Celebration> celebrations = extractCelebrations(result);
                if (celebrations.isEmpty()) {
                    throw new NoEntityException(ENTITY_NAME, String.valueOf(id));
                }
                return celebrations.iterator().next();
            }
        } catch (SQLException e) {
            IO.printError("Error while retrieving the celebration from database");
            return null;
        }
    }

    @Override
    public List<Celebration> findAll() {
        try (ResultSet result = statement.executeQuery(
                "SELECT * FROM celebrations LEFT JOIN events_celebrations " +
                        "ON celebrations.id = events_celebrations.celebrationId"
        )) {
            return extractCelebrations(result);
        } catch (SQLException e) {
            IO.printError("Error while retrieving celebrations from database");
            return List.of();
        }
    }

    @Override
    public Long save(Celebration entity) {
        if (entity.getId() == null) {
            entity.setId(sequence.next());
        }
        try {
            saveCelebrationStatement.setLong(1, entity.getId());
            saveCelebrationStatement.setString(2, entity.getName());
            saveCelebrationStatement.setString(3, entity.getDescription());
            saveCelebrationStatement.setDate(4, Date.valueOf(entity.getDate()));
            saveCelebrationStatement.setString(5, entity.getPlace());
            saveCelebrationStatement.executeUpdate();
            saveEventCelebrationStatement.setLong(1, entity.getId());
            saveEventCelebrationStatement.setLong(2, entity.getEventId());
            saveEventCelebrationStatement.executeUpdate();
        } catch (SQLException e) {
            IO.printError("Error while saving the celebration to database");
        }
        return entity.getId();
    }

    @Override
    public void deleteById(Long id) {
        try {
            deleteCelebrationByIdStatement.setLong(1, id);
            int affectedRows = deleteCelebrationByIdStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new NoEntityException(ENTITY_NAME, String.valueOf(id));
            }
            deleteEventCelebrationByIdStatement.setLong(1, id);
            deleteEventCelebrationByIdStatement.executeUpdate();
        } catch (SQLException e) {
            IO.printError("Error while deleting the celebration from database");
        }
    }

    @Override
    public void deleteAll() {
        try {
            statement.executeUpdate("DELETE FROM events_celebrations");
            statement.executeUpdate("DELETE FROM celebrations");
        } catch (SQLException e) {
            IO.printError("Error while deleting celebrations from database");
        }
    }

    private List<Celebration> extractCelebrations(ResultSet result) throws SQLException {
        List<Celebration> celebrations = new ArrayList<>();
        while (result.next()) {
            celebrations.add(new Celebration(
                    result.getLong("id"),
                    result.getLong("eventId"),
                    result.getString("name"),
                    result.getString("description"),
                    result.getDate("date").toLocalDate(),
                    result.getString("place")
            ));
        }
        return celebrations;
    }
}
