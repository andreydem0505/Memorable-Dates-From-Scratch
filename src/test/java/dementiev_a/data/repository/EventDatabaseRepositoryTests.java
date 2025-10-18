package dementiev_a.data.repository;

public class EventDatabaseRepositoryTests extends EventRepositoryTestBase {
    @Override
    protected EventRepository createRepository() {
        return EventDatabaseRepository.getInstance();
    }
}
