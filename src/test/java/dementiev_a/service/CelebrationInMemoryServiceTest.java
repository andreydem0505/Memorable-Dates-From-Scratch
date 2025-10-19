package dementiev_a.service;

import dementiev_a.data.repository.CelebrationInMemoryRepository;
import dementiev_a.data.repository.CelebrationRepository;
import dementiev_a.data.repository.EventInMemoryRepository;
import dementiev_a.data.repository.EventRepository;

public class CelebrationInMemoryServiceTest extends CelebrationServiceBaseTest {
    @Override
    protected EventRepository getEventRepository() {
        return EventInMemoryRepository.getInstance();
    }

    @Override
    protected CelebrationRepository getCelebrationRepository() {
        return CelebrationInMemoryRepository.getInstance();
    }
}
