package dementiev_a.data.repository;

import dementiev_a.data.model.Celebration;
import dementiev_a.data.model.Event;
import dementiev_a.data.sequence.EventSequence;
import dementiev_a.exception.NoEntityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EventInMemoryRepositoryTest {
    private EventInMemoryRepository eventRepository;

    @BeforeEach
    void setUp() {
        eventRepository = EventInMemoryRepository.getInstance();
        eventRepository.deleteAll();
    }

    @Test
    void testSaveEvent_WhenValidData_ThenFindByIdReturnsEvent() {
        Event eventCityDay = new Event(EventSequence.getInstance().next(), "День города", "Праздник города", LocalDate.of(2025, 9, 20));
        eventRepository.save(eventCityDay);
        Event found = eventRepository.findById(eventCityDay.getId());
        assertEquals("День города", found.getName());
        assertEquals("Праздник города", found.getDescription());
        assertEquals(LocalDate.of(2025, 9, 20), found.getDate());
    }

    @Test
    void testFindById_WhenEventNotExists_ThenThrowsException() {
        assertThrows(NoEntityException.class, () -> eventRepository.findById(999L));
    }

    @Test
    void testFindAll_WhenMultipleEvents_ThenReturnsAll() {
        Event eventCityDay = new Event(
            EventSequence.getInstance().next(),
            "День города",
            "Праздник города",
            LocalDate.of(2025, 9, 20)
        );
        Event eventVillageDay = new Event(
            EventSequence.getInstance().next(),
            "День села",
            "Праздник села",
            LocalDate.of(2025, 9, 21)
        );
        eventRepository.save(eventCityDay);
        eventRepository.save(eventVillageDay);
        Collection<Event> allEvents = eventRepository.findAll();
        assertEquals(2, allEvents.size());
        assertTrue(allEvents.contains(eventCityDay));
        assertTrue(allEvents.contains(eventVillageDay));
    }

    @Test
    void testDeleteById_WhenEventExists_ThenRemoved() {
        Event eventCityDay = new Event(EventSequence.getInstance().next(), "День города", "Праздник города", LocalDate.of(2025, 9, 20));
        eventRepository.save(eventCityDay);
        eventRepository.deleteById(eventCityDay.getId());
        assertThrows(NoEntityException.class, () -> eventRepository.findById(eventCityDay.getId()));
    }

    @Test
    void testDeleteAll_WhenCalled_ThenRepositoryIsEmpty() {
        Event eventCityDay = new Event(EventSequence.getInstance().next(), "День города", "Праздник города", LocalDate.of(2025, 9, 20));
        eventRepository.save(eventCityDay);
        eventRepository.deleteAll();
        assertTrue(eventRepository.findAll().isEmpty());
    }

    @Test
    void testSaveWithParams_WhenValidData_ThenEventCreated() {
        eventRepository.save("День города", "Праздник города", LocalDate.of(2025, 9, 20));
        assertEquals(1, eventRepository.findAll().size());
    }

    @Test
    void testFindByDate_WhenEventsExist_ThenReturnsCorrectEvents() {
        Event eventCityDay = new Event(
            EventSequence.getInstance().next(),
            "День города",
            "Праздник города",
            LocalDate.of(2025, 9, 20)
        );
        Event eventVillageDay = new Event(
            EventSequence.getInstance().next(),
            "День села",
            "Праздник села",
            LocalDate.of(2025, 9, 21)
        );
        eventRepository.save(eventCityDay);
        eventRepository.save(eventVillageDay);
        Collection<Event> foundEvents = eventRepository.findByDate(LocalDate.of(2025, 9, 20));
        assertEquals(1, foundEvents.size());
        assertTrue(foundEvents.contains(eventCityDay));
        assertFalse(foundEvents.contains(eventVillageDay));
    }

    @Test
    void testFindCelebrationsByEventId_WhenNoCelebrations_ThenReturnsEmptySet() {
        Event eventCityDay = new Event(
            EventSequence.getInstance().next(),
            "День города",
            "Праздник города",
            LocalDate.of(2025, 9, 20)
        );
        eventRepository.save(eventCityDay);
        Set<Celebration> celebrations = eventRepository.findCelebrationsByEventId(eventCityDay.getId());
        assertEquals(0, celebrations.size());
    }
}
