package dementiev_a.data.repository;

import dementiev_a.data.model.Celebration;
import dementiev_a.data.sequence.CelebrationSequence;
import dementiev_a.exception.NoEntityException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CelebrationInMemoryRepository implements CelebrationRepository {
    @Getter(lazy = true)
    private static final CelebrationInMemoryRepository instance = new CelebrationInMemoryRepository();

    private static final String ENTITY_NAME = "Celebration";

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
    public Set<Celebration> findAll() {
        return new HashSet<>(storage.values());
    }

    @Override
    public Long save(Celebration entity) {
        if (entity.getId() == null) {
            entity.setId(celebrationSequence.next());
        }
        storage.put(entity.getId(), entity);
        return entity.getId();
    }

    @Override
    public void deleteById(Long id) {
        remove(id);
    }

    @Override
    public void deleteAll() {
        storage.clear();
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

    @Override
    public void deleteAllByIds(Collection<Long> ids) {
        ids.forEach(this::remove);
    }

    private void remove(long id) {
        if (!storage.containsKey(id)) {
            throw new NoEntityException(ENTITY_NAME, String.valueOf(id));
        }
        storage.remove(id);
    }
}
