package dementiev_a.data.repository;

import dementiev_a.data.model.Event;
import dementiev_a.exception.NoEntityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class EventRepositoryTestBase {
    protected EventRepository repository;

    protected abstract EventRepository createRepository();

    @BeforeEach
    void setUp() {
        repository = createRepository();
        repository.deleteAll();
    }

    @Test
    void testSave_WhenNewEvent_ThenAssignedIdAndStored() {
        Event event = new Event("Victory Day", "Commemoration of victory", LocalDate.of(1945, 5, 9));

        Long assignedId = repository.save(event);

        assertNotNull(assignedId, "Saved event should be assigned a non-null id");
        assertEquals(assignedId, event.getId(), "Event.getId() should reflect the id returned from save");

        Event loadedEvent = repository.findById(assignedId);
        assertEquals(event.getId(), loadedEvent.getId(), "Repository should store and return event with same id");
        assertEquals(event.getName(), loadedEvent.getName(),
                "Repository should store and return event with same name");
        assertEquals(event.getDescription(), loadedEvent.getDescription(),
                "Repository should store and return event with same description");
        assertEquals(event.getDate(), loadedEvent.getDate(),
                "Repository should store and return event with same date");
    }

    @Test
    void testFindById_WhenNotExists_ThenThrowNoEntityException() {
        assertThrows(NoEntityException.class,
                () -> repository.findById(999L),
                "Finding non-existing event by id should throw NoEntityException");
    }

    @Test
    void testDeleteById_WhenExists_ThenRemoved() {
        Event event = new Event("Birthday", "Personal birthday", LocalDate.of(1990, 1, 1));
        Long id = repository.save(event);

        repository.deleteById(id);

        assertThrows(NoEntityException.class,
                () -> repository.findById(id),
                "After deletion, finding the event by id should throw NoEntityException");
    }

    @Test
    void testDeleteById_WhenNotExists_ThenThrowNoEntityException() {
        assertThrows(NoEntityException.class,
                () -> repository.deleteById(42L),
                "Deleting non-existing event should throw NoEntityException");
    }

    @Test
    void testFindAll_WhenMultipleSaved_ThenReturnAll() {
        Event firstEvent = new Event("New Year", "Start of the year", LocalDate.of(2025, 1, 1));
        Event secondEvent = new Event("Labor Day", "Workers' day", LocalDate.of(2025, 5, 1));

        repository.save(firstEvent);
        repository.save(secondEvent);

        Collection<Event> allEvents = repository.findAll();

        assertEquals(2, allEvents.size(), "findAll should return the number of saved events");
    }

    @Test
    void testFindByDate_WhenMultipleEventsOnDate_ThenReturnThoseEvents() {
        LocalDate leapDate = LocalDate.of(2000, 2, 29);
        Event eventA = new Event("Leap Celebration A", "A meaningful event on leap day", leapDate);
        Event eventB = new Event("Leap Celebration B", "Another meaningful event on leap day", leapDate);
        Event eventC = new Event("Ordinary Day", "Not on leap day", LocalDate.of(2000, 1, 1));

        repository.save(eventA);
        repository.save(eventB);
        repository.save(eventC);

        Collection<Event> foundEvents = repository.findByDate(leapDate);

        assertEquals(2, foundEvents.size(),
                "findByDate should return exactly events that match the requested date");
    }

    @Test
    void testFindCelebrationsIdsByEventId_WhenHasCelebrations_ThenReturnIds() {
        Event event = new Event("Anniversary", "Company anniversary", LocalDate.of(2010, 6, 15));
        event.addCelebrationId(101L);
        event.addCelebrationId(202L);

        Long id = repository.save(event);

        Collection<Long> celebrationIds = repository.findCelebrationsIdsByEventId(id);

        assertEquals(2, celebrationIds.size(),
                "Should return all celebration ids associated with the event");
        assertTrue(celebrationIds.contains(101L) && celebrationIds.contains(202L),
                "Returned set should contain the originally added celebration ids");
    }

    @Test
    void testFindCelebrationsIdsByEventId_WhenEventNotFound_ThenThrowNoEntityException() {
        assertThrows(NoEntityException.class,
                () -> repository.findCelebrationsIdsByEventId(777L),
                "Requesting celebration ids for a non-existing event should throw NoEntityException");
    }

    @Test
    void testDeleteAll_WhenMultipleEventsSaved_ThenAllRemoved() {
        Event firstEvent = new Event("Spring Festival", "Celebration of spring", LocalDate.of(2025, 3, 20));
        Event secondEvent = new Event("Autumn Festival", "Celebration of autumn", LocalDate.of(2025, 9, 22));

        repository.save(firstEvent);
        repository.save(secondEvent);

        repository.deleteAll();

        Collection<Event> allEvents = repository.findAll();
        assertEquals(0, allEvents.size(), "After deleteAll, repository should be empty");
    }
}
