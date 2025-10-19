package dementiev_a.data.repository;

import dementiev_a.data.model.Celebration;

import java.util.Collection;
import java.util.List;

public interface CelebrationRepository extends Repository<Celebration, Long> {
    String ENTITY_NAME = "Celebration";
    List<Celebration> findAllByIds(Collection<Long> ids);
    void deleteAllByIds(Collection<Long> ids);
}
