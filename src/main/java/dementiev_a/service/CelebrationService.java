package dementiev_a.service;

import dementiev_a.data.repository.CelebrationInMemoryRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CelebrationService {
    @Getter(lazy = true)
    private static final CelebrationService instance = new CelebrationService();

    private final CelebrationInMemoryRepository celebrationRepository = CelebrationInMemoryRepository.getInstance();


}

