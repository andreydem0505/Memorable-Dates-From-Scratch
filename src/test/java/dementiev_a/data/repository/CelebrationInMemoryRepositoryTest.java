package dementiev_a.data.repository;

public class CelebrationInMemoryRepositoryTest extends CelebrationRepositoryBaseTest {
    @Override
    protected CelebrationRepository createRepository() {
        return CelebrationInMemoryRepository.getInstance();
    }
}
