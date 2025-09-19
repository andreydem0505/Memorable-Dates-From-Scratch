package dementiev_a.service;

import dementiev_a.data.model.Celebration;
import dementiev_a.data.model.Event;
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

    private final EventInMemoryRepository eventInMemoryRepository = EventInMemoryRepository.getInstance();

    public List<Event> getAllEvents() {
        return new ArrayList<>(eventInMemoryRepository.findAll());
    }

    public void addEvent(String name, String description, LocalDate date) {
        eventInMemoryRepository.save(name, description, date);
    }

    public Set<Celebration> getCelebrationsByEventId(Long eventId) {
        return eventInMemoryRepository.findCelebrationsByEventId(eventId);
    }
}
