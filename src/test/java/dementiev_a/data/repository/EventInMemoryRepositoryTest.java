package dementiev_a.data.repository;

import dementiev_a.data.model.Event;
import dementiev_a.exception.NoEntityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class EventInMemoryRepositoryTest {

    private EventInMemoryRepository repository;

    @BeforeEach
    void setUp() {
        repository = EventInMemoryRepository.getInstance();
        repository.deleteAll();
    }

    @Test
    void testSave_WhenNewEvent_ThenAssignedIdAndStored() {
        Event event = new Event("Victory Day", "Commemoration of victory", LocalDate.of(1945, 5, 9));

        Long assignedId = repository.save(event);

        assertNotNull(assignedId, "Saved event should be assigned a non-null id");
        assertEquals(assignedId, event.getId(), "Event.getId() should reflect the id returned from save");

        Event loadedEvent = repository.findById(assignedId);
        assertSame(event, loadedEvent, "Repository should store and return the same event instance");
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
        assertTrue(allEvents.contains(firstEvent), "findAll result should contain the first saved event instance");
        assertTrue(allEvents.contains(secondEvent), "findAll result should contain the second saved event instance");
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

        Set<Event> found = repository.findByDate(leapDate);

        assertEquals(2, found.size(),
                "findByDate should return exactly events that match the requested date");
        assertTrue(found.contains(eventA), "findByDate result should include the first event on the date");
        assertTrue(found.contains(eventB), "findByDate result should include the second event on the date");
    }

    @Test
    void testFindCelebrationsIdsByEventId_WhenHasCelebrations_ThenReturnIds() {
        Event event = new Event("Anniversary", "Company anniversary", LocalDate.of(2010, 6, 15));
        event.addCelebrationId(101L);
        event.addCelebrationId(202L);

        Long id = repository.save(event);

        Set<Long> celebrationIds = repository.findCelebrationsIdsByEventId(id);

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
}

