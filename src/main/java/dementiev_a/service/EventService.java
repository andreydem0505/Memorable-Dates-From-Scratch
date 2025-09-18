package dementiev_a.service;

import dementiev_a.data.model.Event;
import dementiev_a.data.repository.EventInMemoryRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventService {
    @Getter(lazy = true)
    private static final EventService instance = new EventService();

    private final EventInMemoryRepository eventInMemoryRepository = EventInMemoryRepository.getInstance();

    public List<Event> getAllEvents() {
        return new ArrayList<>(eventInMemoryRepository.findAll());
    }
}
