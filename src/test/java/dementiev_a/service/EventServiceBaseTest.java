package dementiev_a.service;

import dementiev_a.BaseTest;
import dementiev_a.data.model.Celebration;
import dementiev_a.data.model.Event;
import dementiev_a.data.repository.CelebrationRepository;
import dementiev_a.data.repository.EventRepository;
import dementiev_a.exception.NoEntityException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class EventServiceBaseTest extends BaseTest {

    private EventService eventService;
    private EventRepository eventRepository;
    private CelebrationRepository celebrationRepository;

    protected abstract EventRepository getEventRepository();
    protected abstract CelebrationRepository getCelebrationRepository();

    @BeforeEach
    void setUp() {
        eventRepository = getEventRepository();
        celebrationRepository = getCelebrationRepository();
        eventService = EventService.getInstance();
        eventService.setEventRepository(eventRepository);
        eventService.setCelebrationRepository(celebrationRepository);
        eventRepository.deleteAll();
        celebrationRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        eventRepository.deleteAll();
        celebrationRepository.deleteAll();
    }

    @Test
    void testAddEvent_WhenValidEvent_ThenSavedAndRetrievable() {
        Event newYearEvent = new Event("New Year", "New Year celebration", LocalDate.of(2026, 1, 1));

        eventService.addEvent(newYearEvent);

        assertNotNull(newYearEvent.getId(),
                "After adding an event, the event should receive a non-null id");

        Event loadedEvent = eventService.getEventById(newYearEvent.getId());
        assertEquals(newYearEvent.getId(), loadedEvent.getId(),
                "getEventById should return event with the same id");
        assertEquals(newYearEvent.getName(), loadedEvent.getName(),
                "getEventById should return event with the same name");

        List<Event> allEvents = eventService.getAllEvents();
        assertEquals(1, allEvents.size(),
                "getAllEvents should return a list containing the added event");
        assertTrue(allEvents.stream().anyMatch(e -> e.getId().equals(newYearEvent.getId())),
                "getAllEvents result should include the newly added event");
    }

    @Test
    void testAddEvent_WhenMultipleEvents_ThenAllRetrievable() {
        Event firstEvent = new Event("Independence Day", "National holiday", LocalDate.of(2025, 7, 4));
        Event secondEvent = new Event("Thanksgiving", "Family gathering", LocalDate.of(2025, 11, 27));

        eventService.addEvent(firstEvent);
        eventService.addEvent(secondEvent);

        List<Event> allEvents = eventService.getAllEvents();
        assertEquals(2, allEvents.size(),
                "getAllEvents should return all added events");
    }

    @Test
    void testGetEventById_WhenNotFound_ThenThrowNoEntityException() {
        assertThrows(NoEntityException.class,
                () -> eventService.getEventById(12345L),
                "Requesting an event by a non-existing id should result in NoEntityException");
    }

    @Test
    void testGetAllEvents_WhenEmpty_ThenReturnEmptyList() {
        List<Event> allEvents = eventService.getAllEvents();

        assertTrue(allEvents.isEmpty(),
                "getAllEvents should return empty list when repository is empty");
    }

    @Test
    void testGetEventsByDate_WhenSeveralEventsOnDate_ThenReturnMatchingEvents() {
        LocalDate newYearsEve = LocalDate.of(2023, 12, 31);
        Event officeParty = new Event("Office Party", "End of year party", newYearsEve);
        Event familyDinner = new Event("Family Dinner", "Family gathering", newYearsEve);
        Event springFestival = new Event("Spring Festival", "April festival", LocalDate.of(2024, 4, 1));

        eventRepository.save(officeParty);
        eventRepository.save(familyDinner);
        eventRepository.save(springFestival);

        List<Event> foundEvents = eventService.getEventsByDate(newYearsEve);

        assertEquals(2, foundEvents.size(),
                "getEventsByDate should return only events scheduled on the requested date");
        assertTrue(foundEvents.stream().allMatch(e -> e.getDate().equals(newYearsEve)),
                "All returned events should have the requested date");
    }

    @Test
    void testGetEventsByDate_WhenNoEventsOnDate_ThenReturnEmptyList() {
        Event event = new Event("Some Event", "Description", LocalDate.of(2025, 1, 1));
        eventRepository.save(event);

        List<Event> foundEvents = eventService.getEventsByDate(LocalDate.of(2025, 12, 31));

        assertTrue(foundEvents.isEmpty(),
                "getEventsByDate should return empty list when no events match the date");
    }

    @Test
    void testGetCelebrationsByEventId_WhenEventHasCelebrations_ThenReturnCelebrations() {
        Event techConference = new Event("Conference", "Tech conference", LocalDate.of(2025, 10, 5));
        Long eventId = eventRepository.save(techConference);

        Celebration breakfastMeetup = new Celebration(eventId, "Breakfast Meetup",
                "Morning networking breakfast", LocalDate.of(2025, 10, 5), "Lobby");
        Celebration galaDinner = new Celebration(eventId, "Gala Dinner",
                "Evening gala dinner", LocalDate.of(2025, 10, 5), "Grand Hall");

        Long breakfastId = celebrationRepository.save(breakfastMeetup);
        Long dinnerId = celebrationRepository.save(galaDinner);

        techConference.addCelebrationId(breakfastId);
        techConference.addCelebrationId(dinnerId);
        eventRepository.save(techConference);

        List<Celebration> celebrations = eventService.getCelebrationsByEventId(eventId);

        assertEquals(2, celebrations.size(),
                "getCelebrationsByEventId should return all celebrations linked to the event");
        assertTrue(celebrations.stream().anyMatch(c -> c.getId().equals(breakfastId)),
                "Result should contain the breakfast celebration associated with the event");
        assertTrue(celebrations.stream().anyMatch(c -> c.getId().equals(dinnerId)),
                "Result should contain the dinner celebration associated with the event");
    }

    @Test
    void testGetCelebrationsByEventId_WhenEventHasNoCelebrations_ThenReturnEmptyList() {
        Event communityMeetup = new Event("Meetup", "Community meetup", LocalDate.of(2024, 8, 20));
        Long eventId = eventRepository.save(communityMeetup);

        List<Celebration> celebrations = eventService.getCelebrationsByEventId(eventId);

        assertTrue(celebrations.isEmpty(),
                "When an event has no linked celebrations, getCelebrationsByEventId should return empty list");
    }

    @Test
    void testGetCelebrationsByEventId_WhenEventNotFound_ThenThrowNoEntityException() {
        assertThrows(NoEntityException.class,
                () -> eventService.getCelebrationsByEventId(5555L),
                "Requesting celebrations for non-existing event should throw NoEntityException");
    }

    @Test
    void testDeleteEventById_WhenExists_ThenRemovedAndItsCelebrationsRemoved() {
        Event academicSymposium = new Event("Symposium", "Academic symposium", LocalDate.of(2025, 9, 1));
        Long eventId = eventRepository.save(academicSymposium);

        Celebration morningSession = new Celebration(eventId, "Morning Session",
                "Opening talks", LocalDate.of(2025, 9, 1), "Hall A");
        Celebration eveningReception = new Celebration(eventId, "Evening Reception",
                "Networking reception", LocalDate.of(2025, 9, 1), "Lobby");

        Long morningId = celebrationRepository.save(morningSession);
        Long eveningId = celebrationRepository.save(eveningReception);

        academicSymposium.addCelebrationId(morningId);
        academicSymposium.addCelebrationId(eveningId);
        eventRepository.save(academicSymposium);

        assertNotNull(eventRepository.findById(eventId),
                "Event must exist before deletion");
        assertNotNull(celebrationRepository.findById(morningId),
                "Linked celebration must exist before deletion of the event");

        eventService.deleteEventById(eventId);

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
    void testDeleteEventById_WhenNotExists_ThenThrowNoEntityException() {
        assertThrows(NoEntityException.class,
                () -> eventService.deleteEventById(77777L),
                "deleteEventById should throw NoEntityException when event does not exist");
    }

    @Test
    void testDeleteEventById_WhenNoCelebrations_ThenOnlyEventRemoved() {
        Event simpleEvent = new Event("Simple Event", "Event without celebrations", LocalDate.of(2025, 6, 1));
        Long eventId = eventRepository.save(simpleEvent);

        eventService.deleteEventById(eventId);

        assertThrows(NoEntityException.class,
                () -> eventRepository.findById(eventId),
                "Event should be deleted even when it has no celebrations");
    }

    @Test
    void testEditEvent_WhenValidFields_ThenEventUpdatedInRepository() {
        Event techMeetup = new Event("Tech Meetup", "Monthly meetup", LocalDate.of(2025, 7, 10));
        Long eventId = eventRepository.save(techMeetup);

        eventService.editEvent(eventId, "Tech Meetup - Updated",
                "Monthly meetup with guest speaker", LocalDate.of(2025, 7, 11));

        Event updatedEvent = eventRepository.findById(eventId);
        assertEquals("Tech Meetup - Updated", updatedEvent.getName(),
                "After editEvent, event name should be updated in repository");
        assertEquals("Monthly meetup with guest speaker", updatedEvent.getDescription(),
                "After editEvent, event description should be updated in repository");
        assertEquals(LocalDate.of(2025, 7, 11), updatedEvent.getDate(),
                "After editEvent, event date should be updated in repository");
    }

    @Test
    void testEditEvent_WhenNotExists_ThenThrowNoEntityException() {
        assertThrows(NoEntityException.class,
                () -> eventService.editEvent(66666L, "New Name",
                        "New Description", LocalDate.of(2025, 1, 1)),
                "editEvent should throw NoEntityException when event does not exist");
    }

    @Test
    void testEditEvent_WhenChangingAllFields_ThenAllFieldsUpdated() {
        Event workshopEvent = new Event("Workshop", "Basic workshop", LocalDate.of(2025, 8, 5));
        Long eventId = eventRepository.save(workshopEvent);

        eventService.editEvent(eventId, "Advanced Workshop",
                "Advanced level workshop", LocalDate.of(2025, 8, 6));

        Event updatedEvent = eventRepository.findById(eventId);
        assertEquals("Advanced Workshop", updatedEvent.getName(),
                "Name should be completely changed");
        assertEquals("Advanced level workshop", updatedEvent.getDescription(),
                "Description should be completely changed");
        assertEquals(LocalDate.of(2025, 8, 6), updatedEvent.getDate(),
                "Date should be completely changed");
    }

    @Test
    void testEditEvent_WhenEventHasCelebrations_ThenCelebrationsRemainLinked() {
        Event conference = new Event("Conference", "Annual conference", LocalDate.of(2025, 9, 10));
        Long eventId = eventRepository.save(conference);

        Celebration celebration = new Celebration(eventId, "Opening",
                "Opening ceremony", LocalDate.of(2025, 9, 10), "Main Hall");
        Long celebrationId = celebrationRepository.save(celebration);

        conference.addCelebrationId(celebrationId);
        eventRepository.save(conference);

        eventService.editEvent(eventId, "Updated Conference",
                "Updated annual conference", LocalDate.of(2025, 9, 11));

        Event updatedEvent = eventRepository.findById(eventId);
        assertTrue(updatedEvent.getCelebrationIds().contains(celebrationId),
                "After editing event, celebration links should remain intact");
    }
}
