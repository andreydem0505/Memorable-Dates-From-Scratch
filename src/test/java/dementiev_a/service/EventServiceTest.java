package dementiev_a.service;

import dementiev_a.data.model.Event;
import dementiev_a.data.model.Celebration;
import dementiev_a.data.repository.EventInMemoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EventServiceTest {
    private EventService eventService;
    private EventInMemoryRepository eventRepository;

    @BeforeEach
    void setUp() {
        eventService = EventService.getInstance();
        eventRepository = EventInMemoryRepository.getInstance();
        eventRepository.deleteAll();
    }

    @Test
    void testAddEvent_WhenValidData_ThenEventIsSaved() {
        eventService.addEvent("День города", "Праздник города", LocalDate.of(2025, 9, 20));
        List<Event> allEvents = eventService.getAllEvents();
        assertEquals(1, allEvents.size());
        assertEquals("День города", allEvents.get(0).getName());
    }

    @Test
    void testGetEventById_WhenEventExists_ThenReturnsEvent() {
        eventService.addEvent("День города", "Праздник города", LocalDate.of(2025, 9, 20));
        Event event = eventService.getAllEvents().get(0);
        Event found = eventService.getEventById(event.getId());
        assertEquals(event.getName(), found.getName());
        assertEquals(event.getDescription(), found.getDescription());
        assertEquals(event.getDate(), found.getDate());
    }

    @Test
    void testGetAllEvents_WhenMultipleEvents_ThenReturnsAll() {
        eventService.addEvent("День города", "Праздник города", LocalDate.of(2025, 9, 20));
        eventService.addEvent("День села", "Праздник села", LocalDate.of(2025, 9, 21));
        List<Event> allEvents = eventService.getAllEvents();
        assertEquals(2, allEvents.size());
    }

    @Test
    void testGetEventsByDate_WhenEventsExist_ThenReturnsCorrectEvents() {
        eventService.addEvent("День города", "Праздник города", LocalDate.of(2025, 9, 20));
        eventService.addEvent("День села", "Праздник села", LocalDate.of(2025, 9, 21));
        Set<Event> eventsByDate = eventService.getEventsByDate(LocalDate.of(2025, 9, 20));
        assertEquals(1, eventsByDate.size());
        assertEquals("День города", eventsByDate.iterator().next().getName());
    }

    @Test
    void testGetCelebrationsByEventId_WhenNoCelebrations_ThenReturnsEmptySet() {
        eventService.addEvent("День города", "Праздник города", LocalDate.of(2025, 9, 20));
        Event event = eventService.getAllEvents().get(0);
        Set<Celebration> celebrations = eventService.getCelebrationsByEventId(event.getId());
        assertEquals(0, celebrations.size());
    }
}
