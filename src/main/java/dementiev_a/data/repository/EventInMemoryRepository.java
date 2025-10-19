package dementiev_a.data.repository;

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
    @Getter(lazy = true)
    private static final EventInMemoryRepository instance = new EventInMemoryRepository();

    private final Map<Long, Event> storage = new TreeMap<>();
    private final EventSequence eventSequence = EventSequence.getInstance();

    @Override
    public Event findById(Long id) {
        Event event = storage.get(id);
        if (event == null) {
            throw new NoEntityException(ENTITY_NAME, String.valueOf(id));
        }
        return event;
    }

    @Override
    public List<Event> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Long save(Event entity) {
        if (entity.getId() == null) {
            entity.setId(eventSequence.next());
        }
        storage.put(entity.getId(), entity);
        return entity.getId();
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
    public Set<Long> findCelebrationsIdsByEventId(Long eventId) {
        Event event = findById(eventId);
        if (event == null) {
            return Set.of();
        }
        return event.getCelebrationIds();
    }

    @Override
    public List<Event> findByDate(LocalDate date) {
        return storage.values().stream()
                .filter(event -> event.getDate().equals(date))
                .toList();
    }
}
