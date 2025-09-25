package dementiev_a.data.repository;

import dementiev_a.data.model.Event;

import java.time.LocalDate;
import java.util.Set;

public interface EventRepository extends Repository<Event, Long> {
    Set<Long> findCelebrationsIdsByEventId(Long eventId);
    Set<Event> findByDate(LocalDate date);
}
