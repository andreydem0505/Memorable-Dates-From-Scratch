package dementiev_a.data.repository;

public class EventInMemoryRepositoryTests extends EventRepositoryTestBase {
    @Override
    protected EventRepository createRepository() {
        return EventInMemoryRepository.getInstance();
    }
}
