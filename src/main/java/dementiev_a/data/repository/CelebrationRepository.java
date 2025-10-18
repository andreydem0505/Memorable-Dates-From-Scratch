package dementiev_a.data.repository;

import dementiev_a.data.model.Celebration;

import java.util.Collection;
import java.util.Set;

public interface CelebrationRepository extends Repository<Celebration, Long> {
    Set<Celebration> findAllByIds(Collection<Long> ids);
    void deleteAllByIds(Collection<Long> ids);
}
