package dementiev_a.data.repository;

import dementiev_a.data.model.Celebration;
import dementiev_a.data.model.Event;
import dementiev_a.data.sequence.CelebrationSequence;
import dementiev_a.exception.NoEntityException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CelebrationInMemoryRepository implements CelebrationRepository {
    @Getter
    private static final CelebrationInMemoryRepository instance = new CelebrationInMemoryRepository();

    private static final String ENTITY_NAME = "Отмечание";
    private static final EventInMemoryRepository eventRepository = EventInMemoryRepository.getInstance();

    private final Map<Long, Celebration> storage = new HashMap<>();
    private final CelebrationSequence celebrationSequence = CelebrationSequence.getInstance();

    @Override
    public Celebration findById(Long id) {
        Celebration celebration = storage.get(id);
        if (celebration == null) {
            throw new NoEntityException(ENTITY_NAME, String.valueOf(id));
        }
        return celebration;
    }

    @Override
    public Collection<Celebration> findAll() {
        return storage.values();
    }

    @Override
    public void save(Celebration entity) {
        Event event = eventRepository.findById(entity.getEventId());
        event.addCelebrationId(entity.getId());
        eventRepository.save(event);
        storage.put(entity.getId(), entity);
    }

    @Override
    public void deleteById(Long id) {
        if (!storage.containsKey(id)) {
            throw new NoEntityException(ENTITY_NAME, String.valueOf(id));
        }
        storage.remove(id);
    }

    @Override
    public void deleteAll() {
        storage.clear();
    }

    @Override
    public void save(long eventId, String name, String description, LocalDate date, String place) {
        save(new Celebration(celebrationSequence.next(), eventId, name, description, date, place));
    }

    @Override
    public void deleteByIds(Collection<Long> ids) {
        ids.forEach(storage::remove);
    }

    @Override
    public Set<Celebration> findAllByIds(Collection<Long> ids) {
        Set<Celebration> result = new HashSet<>();
        ids.forEach(id -> {
            if (storage.containsKey(id)) {
                result.add(storage.get(id));
            }
        });
        return result;
    }
}
