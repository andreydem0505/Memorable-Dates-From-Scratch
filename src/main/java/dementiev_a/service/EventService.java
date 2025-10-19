package dementiev_a.service;

import dementiev_a.data.model.Celebration;
import dementiev_a.data.model.Event;
import dementiev_a.data.repository.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventService implements Service {
    @Getter(lazy = true)
    private static final EventService instance = new EventService();

    @Setter
    private EventRepository eventRepository = EventDatabaseRepository.getInstance();
    @Setter
    private CelebrationRepository celebrationRepository = CelebrationDatabaseRepository.getInstance();

    public List<Event> getAllEvents() {
        return new ArrayList<>(eventRepository.findAll());
    }

    public Event getEventById(long id) {
        return eventRepository.findById(id);
    }

    public void addEvent(Event event) {
        eventRepository.save(event);
    }

    public List<Celebration> getCelebrationsByEventId(Long eventId) {
        Set<Long> celebrationsIds = eventRepository.findCelebrationsIdsByEventId(eventId);
        return celebrationRepository.findAllByIds(celebrationsIds);
    }

    public List<Event> getEventsByDate(LocalDate date) {
        return eventRepository.findByDate(date);
    }

    public void deleteEventById(long id) {
        Set<Long> celebrationIds = eventRepository.findById(id).getCelebrationIds();
        celebrationRepository.deleteAllByIds(celebrationIds);
        eventRepository.deleteById(id);
    }

    public void editEvent(long eventId, String name, String description, LocalDate date) {
        Event event = eventRepository.findById(eventId);
        event.setName(name);
        event.setDescription(description);
        event.setDate(date);
        eventRepository.save(event);
    }
}
