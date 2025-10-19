package dementiev_a.service;

import dementiev_a.data.repository.CelebrationDatabaseRepository;
import dementiev_a.data.repository.CelebrationRepository;
import dementiev_a.data.repository.EventDatabaseRepository;
import dementiev_a.data.repository.EventRepository;

public class EventDatabaseServiceTest extends EventServiceBaseTest {
    @Override
    protected EventRepository getEventRepository() {
        return EventDatabaseRepository.getInstance();
    }

    @Override
    protected CelebrationRepository getCelebrationRepository() {
        return CelebrationDatabaseRepository.getInstance();
    }
}
