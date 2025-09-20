package dementiev_a.service;

import dementiev_a.data.model.Celebration;
import dementiev_a.data.model.Event;
import dementiev_a.data.sequence.EventSequence;
import dementiev_a.data.repository.CelebrationInMemoryRepository;
import dementiev_a.data.repository.EventInMemoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CelebrationServiceTest {
    private CelebrationService celebrationService;
    private CelebrationInMemoryRepository celebrationRepository;
    private EventInMemoryRepository eventRepository;
    private Event eventCityDay;

    @BeforeEach
    void setUp() {
        celebrationService = CelebrationService.getInstance();
        celebrationRepository = CelebrationInMemoryRepository.getInstance();
        eventRepository = EventInMemoryRepository.getInstance();
        celebrationRepository.deleteAll();
        eventRepository.deleteAll();
        eventCityDay = new Event(EventSequence.getInstance().next(), "День города", "Праздник города", LocalDate.of(2025, 9, 20));
        eventRepository.save(eventCityDay);
    }

    @Test
    void testAddCelebration_WhenValidData_ThenCelebrationIsSaved() {
        celebrationService.addCelebration(eventCityDay.getId(), "Празднование в парке", "С друзьями", LocalDate.of(2025, 9, 20), "Парк");
        List<Celebration> allCelebrations = List.copyOf(celebrationRepository.findAll());
        assertEquals(1, allCelebrations.size());
        Celebration celebration = allCelebrations.get(0);
        assertEquals("Празднование в парке", celebration.getName());
        assertEquals(eventCityDay.getId(), celebration.getEventId());
        assertEquals("С друзьями", celebration.getDescription());
        assertEquals(LocalDate.of(2025, 9, 20), celebration.getDate());
        assertEquals("Парк", celebration.getPlace());
    }

    @Test
    void testAddCelebration_WhenEventNotExists_ThenThrowsException() {
        long nonExistentEventId = 999L;
        assertThrows(Exception.class, () -> celebrationService.addCelebration(
            nonExistentEventId,
            "Празднование",
            "Описание",
            LocalDate.of(2025, 9, 20),
            "Парк"
        ));
    }

    @Test
    void testAddMultipleCelebrations_WhenValidData_ThenAllAreSaved() {
        celebrationService.addCelebration(
            eventCityDay.getId(),
            "Празднование в парке",
            "С друзьями",
            LocalDate.of(2025, 9, 20),
            "Парк"
        );
        celebrationService.addCelebration(
            eventCityDay.getId(),
            "Празднование на площади",
            "С семьёй",
            LocalDate.of(2025, 9, 21),
            "Площадь"
        );
        List<Celebration> allCelebrations = List.copyOf(celebrationRepository.findAll());
        assertEquals(2, allCelebrations.size());
        assertTrue(allCelebrations.stream().anyMatch(c -> c.getName().equals("Празднование в парке")));
        assertTrue(allCelebrations.stream().anyMatch(c -> c.getName().equals("Празднование на площади")));
    }

    @Test
    void testCelebrationLinkedToEvent_WhenSaved() {
        celebrationService.addCelebration(eventCityDay.getId(), "Празднование в парке", "С друзьями", LocalDate.of(2025, 9, 20), "Парк");
        Event updatedEvent = eventRepository.findById(eventCityDay.getId());
        assertEquals(1, updatedEvent.getCelebrationIds().size());
    }
}
