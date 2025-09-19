package dementiev_a.data.repository;

import dementiev_a.data.model.Celebration;
import dementiev_a.data.model.Event;
import dementiev_a.data.sequence.EventSequence;
import dementiev_a.exception.NoEntityException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventInMemoryRepository implements EventRepository {
    @Getter
    private static final EventInMemoryRepository instance = new EventInMemoryRepository();

    private static final String ENTITY_NAME = "Памятная дата";

    private final Map<Long, Event> storage = new HashMap<>();
    private final EventSequence eventSequence = EventSequence.getInstance();
    private final CelebrationInMemoryRepository celebrationRepository = CelebrationInMemoryRepository.getInstance();

    @Override
    public Event findById(Long id) {
        Event event = storage.get(id);
        if (event == null) {
            throw new NoEntityException(ENTITY_NAME, String.valueOf(id));
        }
        return event;
    }

    @Override
    public Collection<Event> findAll() {
        return storage.values();
    }

    @Override
    public void save(Event entity) {
        storage.put(entity.getId(), entity);
    }

    @Override
    public void deleteById(Long id) {
        if (!storage.containsKey(id)) {
            throw new NoEntityException(ENTITY_NAME, String.valueOf(id));
        }
        celebrationRepository.deleteByIds(storage.get(id).getCelebrationIds());
        storage.remove(id);
    }

    @Override
    public void deleteAll() {
        storage.clear();
        celebrationRepository.deleteAll();
    }

    @Override
    public void save(String name, String description, LocalDate date) {
        save(new Event(eventSequence.next(), name, description, date));
    }

    @Override
    public Set<Celebration> findCelebrationsByEventId(Long eventId) {
        Event event = findById(eventId);
        if (event == null) {
            return Set.of();
        }
        Set<Long> celebrationIds = event.getCelebrationIds();
        return celebrationRepository.findAllByIds(celebrationIds);
    }
}
