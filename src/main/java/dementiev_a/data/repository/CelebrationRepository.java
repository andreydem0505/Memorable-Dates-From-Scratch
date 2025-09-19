package dementiev_a.data.repository;

import dementiev_a.data.model.Celebration;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

public interface CelebrationRepository extends Repository<Celebration, Long> {
    void save(long eventId, String name, String description, LocalDate date, String place);
    void deleteByIds(Collection<Long> ids);
    Set<Celebration> findAllByIds(Collection<Long> ids);
}
