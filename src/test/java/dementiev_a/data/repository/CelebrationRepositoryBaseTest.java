package dementiev_a.data.repository;

import dementiev_a.BaseTest;
import dementiev_a.data.model.Celebration;
import dementiev_a.exception.NoEntityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public abstract class CelebrationRepositoryBaseTest extends BaseTest {

    protected CelebrationRepository repository;

    protected abstract CelebrationRepository createRepository();

    @BeforeEach
    void setUp() {
        repository = createRepository();
        repository.deleteAll();
    }

    @Test
    void testSave_WhenNewCelebration_ThenAssignedIdAndStored() {
        Celebration newCelebration = new Celebration(1L, "Company Anniversary",
                "Annual company anniversary celebration", LocalDate.of(2010, 6, 15), "Main Hall");

        Long assignedId = repository.save(newCelebration);

        assertNotNull(assignedId, "Saved celebration should receive a non-null id");
        assertEquals(assignedId, newCelebration.getId(),
                "Celebration.getId() should match the id returned by save");

        Celebration loadedCelebration = repository.findById(assignedId);
        assertEquals(newCelebration.getName(), loadedCelebration.getName(),
                "Repository should return celebration with the same name");
        assertEquals(newCelebration.getDescription(), loadedCelebration.getDescription(),
                "Repository should return celebration with the same description");
        assertEquals(newCelebration.getDate(), loadedCelebration.getDate(),
                "Repository should return celebration with the same date");
        assertEquals(newCelebration.getPlace(), loadedCelebration.getPlace(),
                "Repository should return celebration with the same place");
    }

    @Test
    void testFindById_WhenNotExists_ThenThrowNoEntityException() {
        assertThrows(NoEntityException.class,
                () -> repository.findById(999L),
                "Finding a celebration by a non-existing id should throw NoEntityException");
    }

    @Test
    void testDeleteById_WhenExists_ThenRemoved() {
        Celebration teamMeeting = new Celebration(2L, "Team Meeting",
                "Quarterly team meeting", LocalDate.of(2024, 3, 10), "Conference Room");
        Long celebrationId = repository.save(teamMeeting);

        repository.deleteById(celebrationId);

        assertThrows(NoEntityException.class,
                () -> repository.findById(celebrationId),
                "After deletion, finding the celebration by id should throw NoEntityException");
    }

    @Test
    void testDeleteById_WhenNotExists_ThenThrowNoEntityException() {
        assertThrows(NoEntityException.class,
                () -> repository.deleteById(42L),
                "Deleting a non-existing celebration should throw NoEntityException");
    }

    @Test
    void testFindAll_WhenMultipleSaved_ThenReturnAll() {
        Celebration productLaunch = new Celebration(3L, "Product Launch",
                "Launch event for new product", LocalDate.of(2025, 9, 1), "Expo Center");
        Celebration charityGala = new Celebration(4L, "Charity Gala",
                "Annual charity fundraising gala", LocalDate.of(2025, 11, 20), "Grand Hotel");

        repository.save(productLaunch);
        repository.save(charityGala);

        List<Celebration> allCelebrations = repository.findAll();

        assertEquals(2, allCelebrations.size(), "findAll should return all saved celebrations");
        assertTrue(allCelebrations.stream().anyMatch(c -> c.getName().equals("Product Launch")),
                "findAll result should include the first saved celebration");
        assertTrue(allCelebrations.stream().anyMatch(c -> c.getName().equals("Charity Gala")),
                "findAll result should include the second saved celebration");
    }

    @Test
    void testFindAll_WhenEmpty_ThenReturnEmptyCollection() {
        List<Celebration> allCelebrations = repository.findAll();

        assertTrue(allCelebrations.isEmpty(),
                "findAll should return empty collection when repository is empty");
    }

    @Test
    void testFindAllByIds_WhenSomeIdsExist_ThenReturnExistingCelebrations() {
        Celebration boardMeeting = new Celebration(5L, "Board Meeting",
                "Monthly board meeting", LocalDate.of(2025, 2, 5), "Board Room");
        Celebration boardDinner = new Celebration(5L, "Board Dinner",
                "Dinner after meeting", LocalDate.of(2025, 2, 5), "Restaurant");
        Celebration hackathon = new Celebration(6L, "Hackathon",
                "24-hour internal hackathon", LocalDate.of(2025, 4, 12), "Office Space");

        Long meetingId = repository.save(boardMeeting);
        Long dinnerId = repository.save(boardDinner);
        repository.save(hackathon);

        Set<Long> queryIds = Set.of(meetingId, dinnerId, 999L);

        List<Celebration> foundCelebrations = repository.findAllByIds(queryIds);

        assertEquals(2, foundCelebrations.size(),
                "findAllByIds should return only celebrations that exist for given ids");
        assertTrue(foundCelebrations.stream().anyMatch(item -> item.getId().equals(meetingId)),
                "Result should contain celebration with meetingId");
        assertTrue(foundCelebrations.stream().anyMatch(item -> item.getId().equals(dinnerId)),
                "Result should contain celebration with dinnerId");
    }

    @Test
    void testFindAllByIds_WhenNoneExist_ThenReturnEmptyCollection() {
        List<Celebration> foundCelebrations = repository.findAllByIds(Set.of(1000L, 2000L));

        assertTrue(foundCelebrations.isEmpty(),
                "findAllByIds should return empty collection when no provided ids match stored celebrations");
    }

    @Test
    void testFindAllByIds_WhenEmptyIdsProvided_ThenReturnEmptyCollection() {
        Celebration celebration = new Celebration(7L, "Some Event",
                "Some description", LocalDate.of(2025, 1, 1), "Some Place");
        repository.save(celebration);

        List<Celebration> foundCelebrations = repository.findAllByIds(List.of());

        assertTrue(foundCelebrations.isEmpty(),
                "findAllByIds should return empty collection when empty ids collection is provided");
    }

    @Test
    void testSave_WhenUpdatingExistingCelebration_ThenReplaceStoredInstance() {
        Celebration originalEvent = new Celebration(7L, "Original Event",
                "Initial description", LocalDate.of(2023, 7, 7), "Old Place");
        Long storedId = repository.save(originalEvent);

        Celebration updatedEvent = new Celebration(storedId, 7L, "Updated Event",
                "Updated description", LocalDate.of(2023, 7, 7), "New Place");

        Long returnedId = repository.save(updatedEvent);

        assertEquals(storedId, returnedId,
                "Saving an entity with existing id should return the same id");
        Celebration loadedCelebration = repository.findById(storedId);
        assertEquals("Updated Event", loadedCelebration.getName(),
                "After updating, repository should store the new name");
        assertEquals("New Place", loadedCelebration.getPlace(),
                "After updating, repository should store the new place");
    }

    @Test
    void testDeleteAllByIds_WhenMultipleIdsProvided_ThenDeleteOnlyThose() {
        Celebration first = new Celebration(8L, "First Event",
                "First description", LocalDate.of(2025, 5, 1), "First Place");
        Celebration second = new Celebration(8L, "Second Event",
                "Second description", LocalDate.of(2025, 6, 1), "Second Place");
        Celebration third = new Celebration(8L, "Third Event",
                "Third description", LocalDate.of(2025, 7, 1), "Third Place");

        Long firstId = repository.save(first);
        Long secondId = repository.save(second);
        Long thirdId = repository.save(third);

        repository.deleteAllByIds(Set.of(firstId, secondId));

        assertThrows(NoEntityException.class,
                () -> repository.findById(firstId),
                "First celebration should be deleted");
        assertThrows(NoEntityException.class,
                () -> repository.findById(secondId),
                "Second celebration should be deleted");
        assertNotNull(repository.findById(thirdId),
                "Third celebration should still exist");
    }

    @Test
    void testDeleteAllByIds_WhenEmptyIdsProvided_ThenNoChanges() {
        Celebration celebration = new Celebration(9L, "Event",
                "Description", LocalDate.of(2025, 1, 1), "Place");
        Long celebrationId = repository.save(celebration);

        repository.deleteAllByIds(List.of());

        assertNotNull(repository.findById(celebrationId),
                "Celebration should still exist after deleteAllByIds with empty collection");
    }

    @Test
    void testDeleteAll_WhenMultipleCelebrationsSaved_ThenAllRemoved() {
        Celebration first = new Celebration(10L, "First",
                "Description", LocalDate.of(2025, 1, 1), "Place");
        Celebration second = new Celebration(10L, "Second",
                "Description", LocalDate.of(2025, 2, 1), "Place");

        repository.save(first);
        repository.save(second);

        repository.deleteAll();

        List<Celebration> allCelebrations = repository.findAll();
        assertEquals(0, allCelebrations.size(), "After deleteAll, repository should be empty");
    }
}
