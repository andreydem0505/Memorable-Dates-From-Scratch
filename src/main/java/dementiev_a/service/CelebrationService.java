package dementiev_a.service;

import dementiev_a.data.model.Celebration;
import dementiev_a.data.model.Event;
import dementiev_a.data.repository.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CelebrationService implements Service {
    @Getter(lazy = true)
    private static final CelebrationService instance = new CelebrationService();

    @Setter
    private EventRepository eventRepository = EventDatabaseRepository.getInstance();
    @Setter
    private CelebrationRepository celebrationRepository = CelebrationDatabaseRepository.getInstance();

    public void addCelebration(long eventId, String name, String description, LocalDate date, String place) {
        Long celebrationId = celebrationRepository.save(new Celebration(eventId, name, description, date, place));
        Event event = eventRepository.findById(eventId);
        event.addCelebrationId(celebrationId);
        eventRepository.save(event);
    }

    public Celebration getCelebrationById(long celebrationId) {
        return celebrationRepository.findById(celebrationId);
    }

    public void deleteCelebrationById(Long id) {
        long eventId = celebrationRepository.findById(id).getEventId();
        Event event = eventRepository.findById(eventId);
        event.getCelebrationIds().remove(id);
        eventRepository.save(event);
        celebrationRepository.deleteById(id);
    }

    public void editCelebration(long celebrationId, String name, String description, LocalDate date, String place) {
        Celebration celebration = celebrationRepository.findById(celebrationId);
        celebration.setName(name);
        celebration.setDescription(description);
        celebration.setDate(date);
        celebration.setPlace(place);
        celebrationRepository.save(celebration);
    }
}
