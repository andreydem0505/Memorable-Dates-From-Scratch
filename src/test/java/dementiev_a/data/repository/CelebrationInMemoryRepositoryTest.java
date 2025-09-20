package dementiev_a.data.repository;

import dementiev_a.data.model.Celebration;
import dementiev_a.data.model.Event;
import dementiev_a.data.sequence.CelebrationSequence;
import dementiev_a.data.sequence.EventSequence;
import dementiev_a.exception.NoEntityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CelebrationInMemoryRepositoryTest {
    private CelebrationInMemoryRepository celebrationRepository;
    private EventInMemoryRepository eventRepository;
    private Event eventCityDay;

    @BeforeEach
    void setUp() {
        celebrationRepository = CelebrationInMemoryRepository.getInstance();
        eventRepository = EventInMemoryRepository.getInstance();
        celebrationRepository.deleteAll();
        eventRepository.deleteAll();
        eventCityDay = new Event(EventSequence.getInstance().next(), "День города", "Праздник города", LocalDate.of(2025, 9, 20));
        eventRepository.save(eventCityDay);
    }

    @Test
    void testSaveCelebration_WhenValidData_ThenFindByIdReturnsCelebration() {
        Celebration celebration = new Celebration(CelebrationSequence.getInstance().next(), eventCityDay.getId(), "Празднование в парке", "С друзьями", LocalDate.of(2025, 9, 20), "Парк");
        celebrationRepository.save(celebration);
        Celebration found = celebrationRepository.findById(celebration.getId());
        assertEquals("Празднование в парке", found.getName());
        assertEquals(eventCityDay.getId(), found.getEventId());
    }

    @Test
    void testFindById_WhenCelebrationNotExists_ThenThrowsException() {
        assertThrows(NoEntityException.class, () -> celebrationRepository.findById(999L));
    }

    @Test
    void testFindAll_WhenMultipleCelebrations_ThenReturnsAll() {
        Celebration celebrationPark = new Celebration(
            CelebrationSequence.getInstance().next(),
            eventCityDay.getId(),
            "Празднование в парке",
            "С друзьями",
            LocalDate.of(2025, 9, 20),
            "Парк"
        );
        Celebration celebrationSquare = new Celebration(
            CelebrationSequence.getInstance().next(),
            eventCityDay.getId(),
            "Празднование на площади",
            "С семьёй",
            LocalDate.of(2025, 9, 21),
            "Площадь"
        );
        celebrationRepository.save(celebrationPark);
        celebrationRepository.save(celebrationSquare);
        List<Celebration> allCelebrations = List.copyOf(celebrationRepository.findAll());
        assertEquals(2, allCelebrations.size());
        assertTrue(allCelebrations.contains(celebrationPark));
        assertTrue(allCelebrations.contains(celebrationSquare));
    }

    @Test
    void testDeleteById_WhenCelebrationExists_ThenRemoved() {
        Celebration celebration = new Celebration(CelebrationSequence.getInstance().next(), eventCityDay.getId(), "Празднование в парке", "С друзьями", LocalDate.of(2025, 9, 20), "Парк");
        celebrationRepository.save(celebration);
        celebrationRepository.deleteById(celebration.getId());
        assertThrows(NoEntityException.class, () -> celebrationRepository.findById(celebration.getId()));
    }

    @Test
    void testDeleteAll_WhenCalled_ThenRepositoryIsEmpty() {
        Celebration celebration = new Celebration(CelebrationSequence.getInstance().next(), eventCityDay.getId(), "Празднование в парке", "С друзьями", LocalDate.of(2025, 9, 20), "Парк");
        celebrationRepository.save(celebration);
        celebrationRepository.deleteAll();
        assertTrue(celebrationRepository.findAll().isEmpty());
    }

    @Test
    void testSaveWithParams_WhenValidData_ThenCelebrationCreated() {
        celebrationRepository.save(eventCityDay.getId(), "Празднование в парке", "С друзьями", LocalDate.of(2025, 9, 20), "Парк");
        assertEquals(1, celebrationRepository.findAll().size());
    }

    @Test
    void testDeleteByIds_WhenMultipleIds_ThenAllRemoved() {
        Celebration celebrationPark = new Celebration(
            CelebrationSequence.getInstance().next(),
            eventCityDay.getId(),
            "Празднование в парке",
            "С друзьями",
            LocalDate.of(2025, 9, 20),
            "Парк"
        );
        Celebration celebrationSquare = new Celebration(
            CelebrationSequence.getInstance().next(),
            eventCityDay.getId(),
            "Празднование на площади",
            "С семьёй",
            LocalDate.of(2025, 9, 21),
            "Площадь"
        );
        celebrationRepository.save(celebrationPark);
        celebrationRepository.save(celebrationSquare);
        celebrationRepository.deleteByIds(List.of(celebrationPark.getId(), celebrationSquare.getId()));
        assertEquals(0, celebrationRepository.findAll().size());
    }

    @Test
    void testFindAllByIds_WhenMultipleIds_ThenReturnsCorrectCelebrations() {
        Celebration celebrationPark = new Celebration(
            CelebrationSequence.getInstance().next(),
            eventCityDay.getId(),
            "Празднование в парке",
            "С друзьями",
            LocalDate.of(2025, 9, 20),
            "Парк"
        );
        Celebration celebrationSquare = new Celebration(
            CelebrationSequence.getInstance().next(),
            eventCityDay.getId(),
            "Празднование на площади",
            "С семьёй",
            LocalDate.of(2025, 9, 21),
            "Площадь"
        );
        celebrationRepository.save(celebrationPark);
        celebrationRepository.save(celebrationSquare);
        Set<Celebration> foundCelebrations = celebrationRepository.findAllByIds(
            List.of(celebrationPark.getId(), celebrationSquare.getId())
        );
        assertEquals(2, foundCelebrations.size());
        assertTrue(foundCelebrations.contains(celebrationPark));
        assertTrue(foundCelebrations.contains(celebrationSquare));
    }

    @Test
    void testEventCelebrationIdsAreUpdated_WhenCelebrationSaved() {
        Celebration celebration = new Celebration(CelebrationSequence.getInstance().next(), eventCityDay.getId(), "Празднование в парке", "С друзьями", LocalDate.of(2025, 9, 20), "Парк");
        celebrationRepository.save(celebration);
        Event updatedEvent = eventRepository.findById(eventCityDay.getId());
        assertTrue(updatedEvent.getCelebrationIds().contains(celebration.getId()));
    }
}
