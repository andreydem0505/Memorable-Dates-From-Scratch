package dementiev_a.data.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Event extends Model<Long> {
    private String name;
    private String description;
    private LocalDate date;
    private Set<Long> celebrationIds = new HashSet<>();

    public Event(String name, String description, LocalDate date) {
        this.name = name;
        this.description = description;
        this.date = date;
    }

    public Event(Long id, String name, String description, LocalDate date, Set<Long> celebrationIds) {
        this.setId(id);
        this.name = name;
        this.description = description;
        this.date = date;
        this.celebrationIds = celebrationIds;
    }

    public void addCelebrationId(Long celebrationId) {
        this.celebrationIds.add(celebrationId);
    }
}
