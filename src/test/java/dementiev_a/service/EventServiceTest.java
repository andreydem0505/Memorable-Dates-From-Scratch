package dementiev_a.service;

import dementiev_a.data.model.Celebration;
import dementiev_a.data.model.Event;
import dementiev_a.data.repository.CelebrationInMemoryRepository;
import dementiev_a.data.repository.EventInMemoryRepository;
import dementiev_a.exception.NoEntityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class EventServiceTest {

    private EventService eventService;
    private EventInMemoryRepository eventRepository;
    private CelebrationInMemoryRepository celebrationRepository;

    @BeforeEach
    void setUp() {
        eventRepository = EventInMemoryRepository.getInstance();
        celebrationRepository = CelebrationInMemoryRepository.getInstance();
        eventRepository.deleteAll();
        celebrationRepository.deleteAll();
        eventService = EventService.getInstance();
    }

    @Test
    void testAddEvent_WhenValidEvent_ThenSavedAndRetrievable() {
        Event newYear = new Event("New Year", "New Year celebration", LocalDate.of(2026, 1, 1));

        eventService.addEvent(newYear);

        assertNotNull(newYear.getId(),
                "After adding an event, the event should receive a non-null id");

        Event loaded = eventService.getEventById(newYear.getId());
        assertSame(newYear, loaded,
                "getEventById should return the same Event instance that was added");

        List<Event> allEvents = eventService.getAllEvents();
        assertEquals(1, allEvents.size(),
                "getAllEvents should return a list containing the added event");
        assertTrue(allEvents.contains(newYear),
                "getAllEvents result should include the newly added event");
    }

    @Test
    void testGetEventById_WhenNotFound_ThenThrowNoEntityException() {
        assertThrows(NoEntityException.class,
                () -> eventService.getEventById(12345L),
                "Requesting an event by a non-existing id should result in NoEntityException");
    }

    @Test
    void testGetEventsByDate_WhenSeveralEventsOnDate_ThenReturnMatchingEvents() {
        LocalDate date = LocalDate.of(2023, 12, 31);
        Event partyA = new Event("Office Party", "End of year party", date);
        Event partyB = new Event("Family Dinner", "Family gathering", date);
        Event other = new Event("Spring Festival", "April festival", LocalDate.of(2024, 4, 1));

        eventRepository.save(partyA);
        eventRepository.save(partyB);
        eventRepository.save(other);

        Set<Event> found = eventService.getEventsByDate(date);

        assertEquals(2, found.size(),
                "getEventsByDate should return only events scheduled on the requested date");
        assertTrue(found.contains(partyA) && found.contains(partyB),
                "Returned set should include both events scheduled on the requested date");
    }

    @Test
    void testGetCelebrationsByEventId_WhenEventHasCelebrations_ThenReturnCelebrations() {
        Event conference = new Event("Conference", "Tech conference", LocalDate.of(2025, 10, 5));
        Long eventId = eventRepository.save(conference);

        Celebration breakfast = new Celebration(eventId, "Breakfast Meetup",
                "Morning networking breakfast", LocalDate.of(2025, 10, 5), "Lobby");
        Celebration dinner = new Celebration(eventId, "Gala Dinner",
                "Evening gala dinner", LocalDate.of(2025, 10, 5), "Grand Hall");

        Long breakfastId = celebrationRepository.save(breakfast);
        Long dinnerId = celebrationRepository.save(dinner);

        // Link celebrations to the event
        conference.addCelebrationId(breakfastId);
        conference.addCelebrationId(dinnerId);

        Set<Celebration> celebrations = eventService.getCelebrationsByEventId(eventId);

        assertEquals(2, celebrations.size(),
                "getCelebrationsByEventId should return all celebrations linked to the event");
        assertTrue(celebrations.stream().anyMatch(c -> c.getId().equals(breakfastId)),
                "Result should contain the breakfast celebration associated with the event");
        assertTrue(celebrations.stream().anyMatch(c -> c.getId().equals(dinnerId)),
                "Result should contain the dinner celebration associated with the event");
    }

    @Test
    void testGetCelebrationsByEventId_WhenEventHasNoCelebrations_ThenReturnEmptySet() {
        Event meetup = new Event("Meetup", "Community meetup", LocalDate.of(2024, 8, 20));
        Long eventId = eventRepository.save(meetup);

        Set<Celebration> celebrations = eventService.getCelebrationsByEventId(eventId);

        assertTrue(celebrations.isEmpty(),
                "When an event has no linked celebrations, getCelebrationsByEventId should return an empty set");
    }

    @Test
    void testGetCelebrationsByEventId_WhenEventNotFound_ThenThrowNoEntityException() {
        assertThrows(NoEntityException.class,
                () -> eventService.getCelebrationsByEventId(5555L),
                "Requesting celebrations for non-existing event should throw NoEntityException");
    }

    @Test
    void testDeleteEventById_WhenExists_ThenRemovedAndItsCelebrationsRemoved() {
        Event symposium = new Event("Symposium", "Academic symposium", LocalDate.of(2025, 9, 1));
        Long eventId = eventRepository.save(symposium);

        // Create two celebrations linked to the event
        Celebration morning = new Celebration(eventId, "Morning Session",
                "Opening talks", LocalDate.of(2025, 9, 1), "Hall A");
        Celebration evening = new Celebration(eventId, "Evening Reception",
                "Networking reception", LocalDate.of(2025, 9, 1), "Lobby");

        Long morningId = celebrationRepository.save(morning);
        Long eveningId = celebrationRepository.save(evening);

        // Link celebrations to the event and persist the event
        symposium.addCelebrationId(morningId);
        symposium.addCelebrationId(eveningId);
        eventRepository.save(symposium);

        // Pre-conditions
        assertNotNull(eventRepository.findById(eventId),
                "Event must exist before deletion");
        assertNotNull(celebrationRepository.findById(morningId),
                "Linked celebration must exist before deletion of the event");

        // Perform deletion
        eventService.deleteEventById(eventId);

        // After deletion, event and linked celebrations should be removed
        assertThrows(NoEntityException.class,
                () -> eventRepository.findById(eventId),
                "After deleteEventById, finding the event by id should throw NoEntityException");
        assertThrows(NoEntityException.class,
                () -> celebrationRepository.findById(morningId),
                "After deleteEventById, linked celebrations should be deleted as well");
        assertThrows(NoEntityException.class,
                () -> celebrationRepository.findById(eveningId),
                "After deleteEventById, linked celebrations should be deleted as well");
    }

    @Test
    void testEditEvent_WhenValidFields_ThenEventUpdatedInRepository() {
        Event meetup = new Event("Tech Meetup", "Monthly meetup", LocalDate.of(2025, 7, 10));
        Long eventId = eventRepository.save(meetup);

        eventService.editEvent(eventId, "Tech Meetup - Updated",
                "Monthly meetup with guest speaker", LocalDate.of(2025, 7, 11));

        Event updated = eventRepository.findById(eventId);
        assertEquals("Tech Meetup - Updated", updated.getName(),
                "After editEvent, event name should be updated in repository");
        assertEquals("Monthly meetup with guest speaker", updated.getDescription(),
                "After editEvent, event description should be updated in repository");
        assertEquals(LocalDate.of(2025, 7, 11), updated.getDate(),
                "After editEvent, event date should be updated in repository");
    }
}
