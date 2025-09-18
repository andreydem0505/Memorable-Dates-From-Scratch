package dementiev_a.data.repository;

import dementiev_a.data.model.Event;
import dementiev_a.exception.NoEntityException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventInMemoryRepository implements EventRepository {
    @Getter(lazy = true)
    private static final EventInMemoryRepository instance = new EventInMemoryRepository();

    private final static String ENTITY_NAME = "Event";

    private final Map<Long, Event> storage = new HashMap<>();

    @Override
    public Event findById(Long id) {
        try {
            return storage.get(id);
        } catch (NullPointerException e) {
            throw new NoEntityException(ENTITY_NAME, String.valueOf(id));
        }
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
        storage.remove(id);
    }

    @Override
    public void deleteAll() {
        storage.clear();
    }
}
