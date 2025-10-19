package dementiev_a.data.repository;

import dementiev_a.BaseTest;
import dementiev_a.data.model.Event;
import dementiev_a.exception.NoEntityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public abstract class EventRepositoryTestBase extends BaseTest {

    protected EventRepository repository;

    protected abstract EventRepository createRepository();

    @BeforeEach
    void setUp() {
        repository = createRepository();
        repository.deleteAll();
    }

    @Test
    void testSave_WhenNewEvent_ThenAssignedIdAndStored() {
        Event newEvent = new Event("Victory Day", "Commemoration of victory", LocalDate.of(1945, 5, 9));

        Long assignedId = repository.save(newEvent);

        assertNotNull(assignedId, "Saved event should be assigned a non-null id");
        assertEquals(assignedId, newEvent.getId(), "Event.getId() should reflect the id returned from save");

        Event loadedEvent = repository.findById(assignedId);
        assertEquals(newEvent.getId(), loadedEvent.getId(), "Repository should store and return event with same id");
        assertEquals(newEvent.getName(), loadedEvent.getName(),
                "Repository should store and return event with same name");
        assertEquals(newEvent.getDescription(), loadedEvent.getDescription(),
                "Repository should store and return event with same description");
        assertEquals(newEvent.getDate(), loadedEvent.getDate(),
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
        Event birthday = new Event("Birthday", "Personal birthday", LocalDate.of(1990, 1, 1));
        Long eventId = repository.save(birthday);

        repository.deleteById(eventId);

        assertThrows(NoEntityException.class,
                () -> repository.findById(eventId),
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
        Event newYear = new Event("New Year", "Start of the year", LocalDate.of(2025, 1, 1));
        Event laborDay = new Event("Labor Day", "Workers' day", LocalDate.of(2025, 5, 1));

        repository.save(newYear);
        repository.save(laborDay);

        List<Event> allEvents = repository.findAll();

        assertEquals(2, allEvents.size(), "findAll should return the number of saved events");
        assertTrue(allEvents.stream().anyMatch(e -> e.getName().equals("New Year")),
                "findAll result should include New Year event");
        assertTrue(allEvents.stream().anyMatch(e -> e.getName().equals("Labor Day")),
                "findAll result should include Labor Day event");
    }

    @Test
    void testFindAll_WhenEmpty_ThenReturnEmptyCollection() {
        List<Event> allEvents = repository.findAll();

        assertTrue(allEvents.isEmpty(), "findAll should return empty collection when repository is empty");
    }

    @Test
    void testFindByDate_WhenMultipleEventsOnDate_ThenReturnThoseEvents() {
        LocalDate leapDate = LocalDate.of(2000, 2, 29);
        Event leapCelebrationA = new Event("Leap Celebration A", "A meaningful event on leap day", leapDate);
        Event leapCelebrationB = new Event("Leap Celebration B", "Another meaningful event on leap day", leapDate);
        Event ordinaryDay = new Event("Ordinary Day", "Not on leap day", LocalDate.of(2000, 1, 1));

        repository.save(leapCelebrationA);
        repository.save(leapCelebrationB);
        repository.save(ordinaryDay);

        List<Event> foundEvents = repository.findByDate(leapDate);

        assertEquals(2, foundEvents.size(),
                "findByDate should return exactly events that match the requested date");
        assertTrue(foundEvents.stream().allMatch(e -> e.getDate().equals(leapDate)),
                "All returned events should have the requested date");
    }

    @Test
    void testFindByDate_WhenNoEventsOnDate_ThenReturnEmptyCollection() {
        Event event = new Event("Some Event", "Description", LocalDate.of(2025, 1, 1));
        repository.save(event);

        List<Event> foundEvents = repository.findByDate(LocalDate.of(2025, 12, 31));

        assertTrue(foundEvents.isEmpty(),
                "findByDate should return empty collection when no events match the date");
    }

    @Test
    void testFindCelebrationsIdsByEventId_WhenHasCelebrations_ThenReturnIds() {
        Event anniversary = new Event("Anniversary", "Company anniversary", LocalDate.of(2010, 6, 15));
        anniversary.addCelebrationId(101L);
        anniversary.addCelebrationId(202L);

        Long eventId = repository.save(anniversary);

        Set<Long> celebrationIds = repository.findCelebrationsIdsByEventId(eventId);

        assertEquals(2, celebrationIds.size(),
                "Should return all celebration ids associated with the event");
        assertTrue(celebrationIds.contains(101L) && celebrationIds.contains(202L),
                "Returned set should contain the originally added celebration ids");
    }

    @Test
    void testFindCelebrationsIdsByEventId_WhenNoCelebrations_ThenReturnEmptyCollection() {
        Event event = new Event("Event", "Event without celebrations", LocalDate.of(2025, 1, 1));
        Long eventId = repository.save(event);

        Set<Long> celebrationIds = repository.findCelebrationsIdsByEventId(eventId);

        assertTrue(celebrationIds.isEmpty(),
                "Should return empty collection when event has no celebrations");
    }

    @Test
    void testFindCelebrationsIdsByEventId_WhenEventNotFound_ThenThrowNoEntityException() {
        assertThrows(NoEntityException.class,
                () -> repository.findCelebrationsIdsByEventId(777L),
                "Requesting celebration ids for a non-existing event should throw NoEntityException");
    }

    @Test
    void testSave_WhenUpdatingExistingEvent_ThenReplaceStoredInstance() {
        Event originalEvent = new Event("Original Event", "Original description", LocalDate.of(2025, 1, 1));
        Long eventId = repository.save(originalEvent);

        Event updatedEvent = new Event(eventId, "Updated Event", "Updated description",
                LocalDate.of(2025, 1, 2), originalEvent.getCelebrationIds());

        Long returnedId = repository.save(updatedEvent);

        assertEquals(eventId, returnedId,
                "Saving an entity with existing id should return the same id");
        Event loadedEvent = repository.findById(eventId);
        assertEquals("Updated Event", loadedEvent.getName(),
                "After updating, repository should store the new name");
        assertEquals("Updated description", loadedEvent.getDescription(),
                "After updating, repository should store the new description");
        assertEquals(LocalDate.of(2025, 1, 2), loadedEvent.getDate(),
                "After updating, repository should store the new date");
    }

    @Test
    void testDeleteAll_WhenMultipleEventsSaved_ThenAllRemoved() {
        Event springFestival = new Event("Spring Festival", "Celebration of spring", LocalDate.of(2025, 3, 20));
        Event autumnFestival = new Event("Autumn Festival", "Celebration of autumn", LocalDate.of(2025, 9, 22));

        repository.save(springFestival);
        repository.save(autumnFestival);

        repository.deleteAll();

        List<Event> allEvents = repository.findAll();
        assertEquals(0, allEvents.size(), "After deleteAll, repository should be empty");
    }
}
