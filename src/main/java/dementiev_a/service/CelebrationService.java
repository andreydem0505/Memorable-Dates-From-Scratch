package dementiev_a.service;

import dementiev_a.data.model.Celebration;
import dementiev_a.data.model.Event;
import dementiev_a.data.repository.CelebrationInMemoryRepository;
import dementiev_a.data.repository.EventInMemoryRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CelebrationService {
    @Getter(lazy = true)
    private static final CelebrationService instance = new CelebrationService();

    private final EventInMemoryRepository eventRepository = EventInMemoryRepository.getInstance();
    private final CelebrationInMemoryRepository celebrationRepository = CelebrationInMemoryRepository.getInstance();

    public void addCelebration(long eventId, String name, String description, LocalDate date, String place) {
        Long celebrationId = celebrationRepository.save(new Celebration(eventId, name, description, date, place));
        Event event = eventRepository.findById(eventId);
        event.addCelebrationId(celebrationId);
        eventRepository.save(event);
    }
}
