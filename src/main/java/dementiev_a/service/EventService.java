package dementiev_a.service;

import dementiev_a.data.model.Celebration;
import dementiev_a.data.model.Event;
import dementiev_a.data.repository.CelebrationInMemoryRepository;
import dementiev_a.data.repository.EventInMemoryRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventService {
    @Getter(lazy = true)
    private static final EventService instance = new EventService();

    private final EventInMemoryRepository eventRepository = EventInMemoryRepository.getInstance();
    private final CelebrationInMemoryRepository celebrationRepository = CelebrationInMemoryRepository.getInstance();

    public List<Event> getAllEvents() {
        return new ArrayList<>(eventRepository.findAll());
    }

    public Event getEventById(long id) {
        return eventRepository.findById(id);
    }

    public void addEvent(Event event) {
        eventRepository.save(event);
    }

    public Set<Celebration> getCelebrationsByEventId(Long eventId) {
        Set<Long> celebrationsIds = eventRepository.findCelebrationsIdsByEventId(eventId);
        return celebrationRepository.findAllByIds(celebrationsIds);
    }

    public Set<Event> getEventsByDate(LocalDate date) {
        return eventRepository.findByDate(date);
    }
}
