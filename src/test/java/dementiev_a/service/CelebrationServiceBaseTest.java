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

public abstract class CelebrationServiceBaseTest extends BaseTest {

    private CelebrationService celebrationService;
    private EventRepository eventRepository;
    private CelebrationRepository celebrationRepository;

    protected abstract EventRepository getEventRepository();
    protected abstract CelebrationRepository getCelebrationRepository();

    @BeforeEach
    void setUp() {
        eventRepository = getEventRepository();
        celebrationRepository = getCelebrationRepository();
        celebrationService = CelebrationService.getInstance();
        celebrationService.setEventRepository(eventRepository);
        celebrationService.setCelebrationRepository(celebrationRepository);
        eventRepository.deleteAll();
        celebrationRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        eventRepository.deleteAll();
        celebrationRepository.deleteAll();
    }

    @Test
    void testAddCelebration_WhenValidEvent_ThenSavedAndLinkedToEvent() {
        Event techConference = new Event("Conference", "Annual tech conference",
                LocalDate.of(2025, 10, 5));
        Long eventId = eventRepository.save(techConference);

        celebrationService.addCelebration(eventId, "Breakfast Meetup",
                "Morning networking breakfast", LocalDate.of(2025, 10, 5), "Lobby");

        List<Celebration> allCelebrations = celebrationRepository.findAll();
        assertEquals(1, allCelebrations.size(),
                "After adding a celebration, repository should contain exactly one celebration");

        Celebration storedCelebration = allCelebrations.iterator().next();
        assertEquals(eventId, Long.valueOf(storedCelebration.getEventId()),
                "Stored celebration.eventId must match the event id provided to the service");
        assertEquals("Breakfast Meetup", storedCelebration.getName(),
                "Stored celebration.name must match the provided name");

        Event linkedEvent = eventRepository.findById(eventId);
        assertTrue(linkedEvent.getCelebrationIds().contains(storedCelebration.getId()),
                "Event should contain the id of the created celebration in its celebrationIds set");
    }

    @Test
    void testAddCelebration_WhenEventNotFound_ThenThrowAndCelebrationStillSaved() {
        long missingEventId = 9999L;

        NoEntityException thrownException = assertThrows(NoEntityException.class,
                () -> celebrationService.addCelebration(missingEventId, "Orphan Celebration",
                        "This celebration references a missing event", LocalDate.of(2025, 1, 1), "Nowhere"),
                "Adding a celebration for a non-existing event should throw NoEntityException");

        assertTrue(thrownException.getMessage().contains(String.valueOf(missingEventId)),
                "Exception message should include the id of the missing entity to aid diagnostics");

        List<Celebration> persistedCelebrations = celebrationRepository.findAll();
        assertEquals(1, persistedCelebrations.size(),
                "Because the service saves celebration before event lookup, the celebration remains persisted");
        Celebration savedCelebration = persistedCelebrations.iterator().next();
        assertEquals(missingEventId, Long.valueOf(savedCelebration.getEventId()),
                "Persisted celebration should retain the provided (missing) eventId");
    }

    @Test
    void testAddMultipleCelebrations_WhenSameEvent_ThenAllCelebrationsLinked() {
        Event cityFestival = new Event("Festival", "City cultural festival", LocalDate.of(2025, 6, 20));
        Long eventId = eventRepository.save(cityFestival);

        celebrationService.addCelebration(eventId, "Opening Ceremony",
                "Official opening", LocalDate.of(2025, 6, 20), "Main Stage");
        celebrationService.addCelebration(eventId, "Fireworks",
                "Evening fireworks show", LocalDate.of(2025, 6, 20), "Riverside");

        List<Celebration> allCelebrations = celebrationRepository.findAll();
        assertEquals(2, allCelebrations.size(),
                "Two celebrations should be stored after adding two celebrations for the same event");

        Event updatedEvent = eventRepository.findById(eventId);
        assertEquals(2, updatedEvent.getCelebrationIds().size(),
                "Event's celebrationIds should contain two entries after adding two celebrations");
    }

    @Test
    void testAddCelebration_WhenFieldsProvided_ThenCelebrationHasCorrectFields() {
        Event boardMeeting = new Event("Board Meeting", "Quarterly board meeting",
                LocalDate.of(2025, 2, 5));
        Long eventId = eventRepository.save(boardMeeting);

        celebrationService.addCelebration(eventId, "Board Dinner",
                "Dinner after the meeting", LocalDate.of(2025, 2, 5), "Private Room");

        Celebration storedCelebration = celebrationRepository.findAll().iterator().next();

        assertEquals("Board Dinner", storedCelebration.getName(),
                "Stored celebration name should match the one supplied to the service");
        assertEquals("Dinner after the meeting", storedCelebration.getDescription(),
                "Stored celebration description should match the one supplied to the service");
        assertEquals(LocalDate.of(2025, 2, 5), storedCelebration.getDate(),
                "Stored celebration date should match the one supplied to the service");
        assertEquals("Private Room", storedCelebration.getPlace(),
                "Stored celebration place should match the one supplied to the service");
    }

    @Test
    void testGetCelebrationById_WhenExists_ThenReturnCelebration() {
        Event charityGala = new Event("Gala", "Charity gala event", LocalDate.of(2025, 11, 15));
        Long eventId = eventRepository.save(charityGala);

        celebrationService.addCelebration(eventId, "Pre-Gala Reception",
                "Reception before the gala", LocalDate.of(2025, 11, 15), "Reception Hall");

        Celebration storedCelebration = celebrationRepository.findAll().iterator().next();
        Celebration fetchedCelebration = celebrationService.getCelebrationById(storedCelebration.getId());

        assertEquals(storedCelebration.getId(), fetchedCelebration.getId(),
                "getCelebrationById should return celebration with the same id");
        assertEquals(storedCelebration.getName(), fetchedCelebration.getName(),
                "getCelebrationById should return celebration with the same name");
        assertEquals(storedCelebration.getDescription(), fetchedCelebration.getDescription(),
                "getCelebrationById should return celebration with the same description");
    }

    @Test
    void testGetCelebrationById_WhenNotExists_ThenThrowNoEntityException() {
        assertThrows(NoEntityException.class,
                () -> celebrationService.getCelebrationById(12345L),
                "getCelebrationById should throw NoEntityException when celebration does not exist");
    }

    @Test
    void testDeleteCelebrationById_WhenExists_ThenRemovedAndUnlinkedFromEvent() {
        Event educationalSeminar = new Event("Seminar", "Educational seminar", LocalDate.of(2025, 3, 10));
        Long eventId = eventRepository.save(educationalSeminar);

        celebrationService.addCelebration(eventId, "Coffee Break",
                "Short coffee break", LocalDate.of(2025, 3, 10), "Lobby");

        Celebration storedCelebration = celebrationRepository.findAll().iterator().next();
        Long celebrationId = storedCelebration.getId();

        Event beforeDeletion = eventRepository.findById(eventId);
        assertTrue(beforeDeletion.getCelebrationIds().contains(celebrationId),
                "Event must contain the celebration id before deletion");

        celebrationService.deleteCelebrationById(celebrationId);

        assertThrows(NoEntityException.class,
                () -> celebrationRepository.findById(celebrationId),
                "After deleteCelebrationById, finding celebration by id should throw NoEntityException");

        Event afterDeletion = eventRepository.findById(eventId);
        assertFalse(afterDeletion.getCelebrationIds().contains(celebrationId),
                "After deleting a celebration, the event's celebrationIds must not contain the deleted id");
    }

    @Test
    void testDeleteCelebrationById_WhenNotExists_ThenThrowNoEntityException() {
        assertThrows(NoEntityException.class,
                () -> celebrationService.deleteCelebrationById(99999L),
                "deleteCelebrationById should throw NoEntityException when celebration does not exist");
    }

    @Test
    void testEditCelebration_WhenValidFields_ThenUpdatedInRepository() {
        Event handsOnWorkshop = new Event("Workshop", "Hands-on workshop", LocalDate.of(2025, 4, 20));
        Long eventId = eventRepository.save(handsOnWorkshop);

        celebrationService.addCelebration(eventId, "Lunch Session",
                "Casual lunch", LocalDate.of(2025, 4, 20), "Cafeteria");

        Celebration storedCelebration = celebrationRepository.findAll().iterator().next();
        Long celebrationId = storedCelebration.getId();

        celebrationService.editCelebration(celebrationId, "Lunch Networking",
                "Networking during lunch", LocalDate.of(2025, 4, 20), "Conference Room");

        Celebration updatedCelebration = celebrationRepository.findById(celebrationId);
        assertEquals("Lunch Networking", updatedCelebration.getName(),
                "After editCelebration, celebration name should be updated in repository");
        assertEquals("Networking during lunch", updatedCelebration.getDescription(),
                "After editCelebration, celebration description should be updated in repository");
        assertEquals(LocalDate.of(2025, 4, 20), updatedCelebration.getDate(),
                "After editCelebration, celebration date should be updated in repository");
        assertEquals("Conference Room", updatedCelebration.getPlace(),
                "After editCelebration, celebration place should be updated in repository");
    }

    @Test
    void testEditCelebration_WhenNotExists_ThenThrowNoEntityException() {
        assertThrows(NoEntityException.class,
                () -> celebrationService.editCelebration(88888L, "New Name",
                        "New Description", LocalDate.of(2025, 1, 1), "New Place"),
                "editCelebration should throw NoEntityException when celebration does not exist");
    }

    @Test
    void testEditCelebration_WhenChangingAllFields_ThenAllFieldsUpdated() {
        Event trainingSession = new Event("Training", "Employee training", LocalDate.of(2025, 5, 15));
        Long eventId = eventRepository.save(trainingSession);

        celebrationService.addCelebration(eventId, "Morning Session",
                "First half of training", LocalDate.of(2025, 5, 15), "Room A");

        Celebration storedCelebration = celebrationRepository.findAll().iterator().next();
        Long celebrationId = storedCelebration.getId();

        celebrationService.editCelebration(celebrationId, "Afternoon Session",
                "Second half of training", LocalDate.of(2025, 5, 16), "Room B");

        Celebration updatedCelebration = celebrationRepository.findById(celebrationId);
        assertEquals("Afternoon Session", updatedCelebration.getName(),
                "Name should be completely changed");
        assertEquals("Second half of training", updatedCelebration.getDescription(),
                "Description should be completely changed");
        assertEquals(LocalDate.of(2025, 5, 16), updatedCelebration.getDate(),
                "Date should be completely changed");
        assertEquals("Room B", updatedCelebration.getPlace(),
                "Place should be completely changed");
        assertEquals(eventId, Long.valueOf(updatedCelebration.getEventId()),
                "EventId should remain unchanged after edit");
    }
}
