package dementiev_a.data.repository;

public class CelebrationDatabaseRepositoryTest extends CelebrationRepositoryBaseTest {
    @Override
    protected CelebrationRepository createRepository() {
        return CelebrationDatabaseRepository.getInstance();
    }
}
