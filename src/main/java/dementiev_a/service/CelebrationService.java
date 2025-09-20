package dementiev_a.service;

import dementiev_a.data.repository.CelebrationInMemoryRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CelebrationService {
    @Getter(lazy = true)
    private static final CelebrationService instance = new CelebrationService();

    private final CelebrationInMemoryRepository celebrationRepository = CelebrationInMemoryRepository.getInstance();

    public void addCelebration(long eventId, String name, String description, LocalDate date, String place) {
        celebrationRepository.save(eventId, name, description, date, place);
    }
}
