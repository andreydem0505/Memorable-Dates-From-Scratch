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

    @Test
    void testGetCelebrationById_WhenExists_ThenReturnCelebration() {
        Event gala = new Event("Gala", "Charity gala event", LocalDate.of(2025, 11, 15));
        Long eventId = eventRepository.save(gala);

        celebrationService.addCelebration(eventId, "Pre-Gala Reception",
                "Reception before the gala", LocalDate.of(2025, 11, 15), "Reception Hall");

        Celebration stored = celebrationRepository.findAll().iterator().next();
        Celebration fetched = celebrationService.getCelebrationById(stored.getId());

        assertSame(stored, fetched,
                "getCelebrationById should return the same Celebration instance stored in repository");
    }

    @Test
    void testDeleteCelebrationById_WhenExists_ThenRemovedAndUnlinkedFromEvent() {
        Event seminar = new Event("Seminar", "Educational seminar", LocalDate.of(2025, 3, 10));
        Long eventId = eventRepository.save(seminar);

        celebrationService.addCelebration(eventId, "Coffee Break",
                "Short coffee break", LocalDate.of(2025, 3, 10), "Lobby");

        Celebration stored = celebrationRepository.findAll().iterator().next();
        Long celebrationId = stored.getId();

        // Ensure event is linked before deletion
        Event beforeDeletion = eventRepository.findById(eventId);
        assertTrue(beforeDeletion.getCelebrationIds().contains(celebrationId),
                "Event must contain the celebration id before deletion");

        celebrationService.deleteCelebrationById(celebrationId);

        // After deletion, celebration must be removed and event must not reference it
        assertThrows(NoEntityException.class,
                () -> celebrationRepository.findById(celebrationId),
                "After deleteCelebrationById, finding celebration by id should throw NoEntityException");

        Event afterDeletion = eventRepository.findById(eventId);
        assertFalse(afterDeletion.getCelebrationIds().contains(celebrationId),
                "After deleting a celebration, the event's celebrationIds must not contain the deleted id");
    }

    @Test
    void testEditCelebration_WhenValidFields_ThenUpdatedInRepository() {
        Event workshop = new Event("Workshop", "Hands-on workshop", LocalDate.of(2025, 4, 20));
        Long eventId = eventRepository.save(workshop);

        celebrationService.addCelebration(eventId, "Lunch Session",
                "Casual lunch", LocalDate.of(2025, 4, 20), "Cafeteria");

        Celebration stored = celebrationRepository.findAll().iterator().next();
        Long celebrationId = stored.getId();

        celebrationService.editCelebration(celebrationId, "Lunch Networking",
                "Networking during lunch", LocalDate.of(2025, 4, 20), "Conference Room");

        Celebration updated = celebrationRepository.findById(celebrationId);
        assertEquals("Lunch Networking", updated.getName(),
                "After editCelebration, celebration name should be updated in repository");
        assertEquals("Networking during lunch", updated.getDescription(),
                "After editCelebration, celebration description should be updated in repository");
        assertEquals(LocalDate.of(2025, 4, 20), updated.getDate(),
                "After editCelebration, celebration date should be updated in repository");
        assertEquals("Conference Room", updated.getPlace(),
                "After editCelebration, celebration place should be updated in repository");
    }
}
