package dementiev_a.data.model;

import lombok.Getter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
public class Event extends Model<Long> {
    private String name;
    private String description;
    private LocalDate date;
    private Set<Long> celebrationIds = new HashSet<>();

    public Event(Long id, String name, String description, LocalDate date) {
        super(id);
        this.name = name;
        this.description = description;
        this.date = date;
    }

    public void addCelebrationId(Long celebrationId) {
        this.celebrationIds.add(celebrationId);
    }
}
