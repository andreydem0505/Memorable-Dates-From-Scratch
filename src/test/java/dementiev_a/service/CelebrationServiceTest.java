package dementiev_a.service;

import dementiev_a.data.model.Celebration;
import dementiev_a.data.model.Event;
import dementiev_a.data.repository.CelebrationInMemoryRepository;
import dementiev_a.data.repository.EventInMemoryRepository;
import dementiev_a.exception.NoEntityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class CelebrationServiceTest {

    private CelebrationService celebrationService;
    private EventInMemoryRepository eventRepository;
    private CelebrationInMemoryRepository celebrationRepository;

    @BeforeEach
    void setUp() {
        eventRepository = EventInMemoryRepository.getInstance();
        celebrationRepository = CelebrationInMemoryRepository.getInstance();
        eventRepository.deleteAll();
        celebrationRepository.deleteAll();
        celebrationService = CelebrationService.getInstance();
    }

    @Test
    void testAddCelebration_WhenValidEvent_ThenSavedAndLinkedToEvent() {
        Event conference = new Event("Conference", "Annual tech conference",
                LocalDate.of(2025, 10, 5));
        Long eventId = eventRepository.save(conference);

        celebrationService.addCelebration(eventId, "Breakfast Meetup",
                "Morning networking breakfast", LocalDate.of(2025, 10, 5), "Lobby");

        // Verify celebration saved in repository
        Collection<Celebration> allCelebrations = celebrationRepository.findAll();
        assertEquals(1, allCelebrations.size(),
                "After adding a celebration, repository should contain exactly one celebration");

        Celebration storedCelebration = allCelebrations.iterator().next();
        assertEquals(eventId, Long.valueOf(storedCelebration.getEventId()),
                "Stored celebration.eventId must match the event id provided to the service");
        assertEquals("Breakfast Meetup", storedCelebration.getName(),
                "Stored celebration.name must match the provided name");

        // Verify event is linked to celebration
        Event linkedEvent = eventRepository.findById(eventId);
        assertTrue(linkedEvent.getCelebrationIds().contains(storedCelebration.getId()),
                "Event should contain the id of the created celebration in its celebrationIds set");
    }

    @Test
    void testAddCelebration_WhenEventNotFound_ThenThrowAndCelebrationStillSaved() {
        long missingEventId = 9999L;

        NoEntityException thrown = assertThrows(NoEntityException.class,
                () -> celebrationService.addCelebration(missingEventId, "Orphan Celebration",
                        "This celebration references a missing event", LocalDate.of(2025, 1, 1), "Nowhere"),
                "Adding a celebration for a non-existing event should throw NoEntityException");

        // Do not rely on localization of the exception message; ensure it mentions the missing id
        assertTrue(thrown.getMessage().contains(String.valueOf(missingEventId)),
                "Exception message should include the id of the missing entity to aid diagnostics");

        // Current implementation saves the celebration before verifying event existence,
        // so a celebration is persisted even when the linked event is missing.
        Collection<Celebration> persisted = celebrationRepository.findAll();
        assertEquals(1, persisted.size(),
                "Because the service saves celebration before event lookup, the celebration remains persisted");
        Celebration savedCelebration = persisted.iterator().next();
        assertEquals(missingEventId, Long.valueOf(savedCelebration.getEventId()),
                "Persisted celebration should retain the provided (missing) eventId");
    }

    @Test
    void testAddMultipleCelebrations_WhenSameEvent_ThenAllCelebrationsLinked() {
        Event festival = new Event("Festival", "City cultural festival", LocalDate.of(2025, 6, 20));
        Long eventId = eventRepository.save(festival);

        celebrationService.addCelebration(eventId, "Opening Ceremony",
                "Official opening", LocalDate.of(2025, 6, 20), "Main Stage");
        celebrationService.addCelebration(eventId, "Fireworks",
                "Evening fireworks show", LocalDate.of(2025, 6, 20), "Riverside");

        // Verify both celebrations saved
        Collection<Celebration> allCelebrations = celebrationRepository.findAll();
        assertEquals(2, allCelebrations.size(),
                "Two celebrations should be stored after adding two celebrations for the same event");

        // Verify event contains both celebration ids
        Event updatedEvent = eventRepository.findById(eventId);
        assertEquals(2, updatedEvent.getCelebrationIds().size(),
                "Event's celebrationIds should contain two entries after adding two celebrations");
    }

    @Test
    void testAddCelebration_WhenFieldsProvided_ThenCelebrationHasCorrectFields() {
        Event meeting = new Event("Board Meeting", "Quarterly board meeting",
                LocalDate.of(2025, 2, 5));
        Long eventId = eventRepository.save(meeting);

        celebrationService.addCelebration(eventId, "Board Dinner",
                "Dinner after the meeting", LocalDate.of(2025, 2, 5), "Private Room");

        Celebration stored = celebrationRepository.findAll().iterator().next();

        assertEquals("Board Dinner", stored.getName(),
                "Stored celebration name should match the one supplied to the service");
        assertEquals("Dinner after the meeting", stored.getDescription(),
                "Stored celebration description should match the one supplied to the service");
        assertEquals(LocalDate.of(2025, 2, 5), stored.getDate(),
                "Stored celebration date should match the one supplied to the service");
        assertEquals("Private Room", stored.getPlace(),
                "Stored celebration place should match the one supplied to the service");
    }
}
