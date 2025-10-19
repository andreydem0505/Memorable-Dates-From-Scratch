package dementiev_a.data.repository;

import dementiev_a.BaseTest;
import dementiev_a.data.model.Celebration;
import dementiev_a.exception.NoEntityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collection;
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
        Celebration celebration = new Celebration(1L, "Company Anniversary",
                "Annual company anniversary celebration", LocalDate.of(2010, 6, 15), "Main Hall");

        Long assignedId = repository.save(celebration);

        assertNotNull(assignedId, "Saved celebration should receive a non-null id");
        assertEquals(assignedId, celebration.getId(),
                "Celebration.getId() should match the id returned by save");

        Celebration loaded = repository.findById(assignedId);
        assertEquals(celebration, loaded, "Repository should return the same celebration instance that was saved");
    }

    @Test
    void testFindById_WhenNotExists_ThenThrowNoEntityException() {
        assertThrows(NoEntityException.class,
                () -> repository.findById(999L),
                "Finding a celebration by a non-existing id should throw NoEntityException");
    }

    @Test
    void testDeleteById_WhenExists_ThenRemoved() {
        Celebration celebration = new Celebration(2L, "Team Meeting",
                "Quarterly team meeting", LocalDate.of(2024, 3, 10), "Conference Room");
        Long id = repository.save(celebration);

        repository.deleteById(id);

        assertThrows(NoEntityException.class,
                () -> repository.findById(id),
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
        Celebration first = new Celebration(3L, "Product Launch",
                "Launch event for new product", LocalDate.of(2025, 9, 1), "Expo Center");
        Celebration second = new Celebration(4L, "Charity Gala",
                "Annual charity fundraising gala", LocalDate.of(2025, 11, 20), "Grand Hotel");

        repository.save(first);
        repository.save(second);

        Collection<Celebration> all = repository.findAll();

        assertEquals(2, all.size(), "findAll should return all saved celebrations");
        assertTrue(all.contains(first), "findAll result should include the first saved celebration");
        assertTrue(all.contains(second), "findAll result should include the second saved celebration");
    }

    @Test
    void testFindAllByIds_WhenSomeIdsExist_ThenReturnExistingCelebrations() {
        Celebration a = new Celebration(5L, "Board Meeting",
                "Monthly board meeting", LocalDate.of(2025, 2, 5), "Board Room");
        Celebration b = new Celebration(5L, "Board Dinner",
                "Dinner after meeting", LocalDate.of(2025, 2, 5), "Restaurant");
        Celebration c = new Celebration(6L, "Hackathon",
                "24-hour internal hackathon", LocalDate.of(2025, 4, 12), "Office Space");

        Long idA = repository.save(a);
        Long idB = repository.save(b);
        repository.save(c);

        Set<Long> queryIds = Set.of(idA, idB, 999L);

        Collection<Celebration> found = repository.findAllByIds(queryIds);

        assertEquals(2, found.size(),
                "findAllByIds should return only celebrations that exist for given ids");
        assertTrue(found.stream().anyMatch(item -> item.getId().equals(idA)),
                "Result should contain celebration with idA");
        assertTrue(found.stream().anyMatch(item -> item.getId().equals(idB)),
                "Result should contain celebration with idB");
    }

    @Test
    void testFindAllByIds_WhenNoneExist_ThenReturnEmptySet() {
        Collection<Celebration> found = repository.findAllByIds(Set.of(1000L, 2000L));

        assertTrue(found.isEmpty(),
                "findAllByIds should return an empty set when no provided ids match stored celebrations");
    }

    @Test
    void testSave_WhenUpdatingExistingCelebration_ThenReplaceStoredInstance() {
        Celebration original = new Celebration(7L, "Original Event",
                "Initial description", LocalDate.of(2023, 7, 7), "Old Place");
        Long storedId = repository.save(original);

        Celebration updated = new Celebration(7L, "Updated Event",
                "Updated description", LocalDate.of(2023, 7, 7), "New Place");
        updated.setId(storedId);

        Long returnedId = repository.save(updated);

        assertEquals(storedId, returnedId,
                "Saving an entity with existing id should return the same id");
        Celebration loaded = repository.findById(storedId);
        assertEquals(updated, loaded,
                "After updating, repository should store and return the new celebration instance");
    }
}

