package dementiev_a.data.repository;

import dementiev_a.data.model.Celebration;
import dementiev_a.data.model.Event;

import java.time.LocalDate;
import java.util.Set;

public interface EventRepository extends Repository<Event, Long> {
    void save(String name, String description, LocalDate date);
    Set<Celebration> findCelebrationsByEventId(Long eventId);
    Set<Event> findByDate(LocalDate date);
}
