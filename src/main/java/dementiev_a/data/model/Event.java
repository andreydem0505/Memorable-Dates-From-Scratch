package dementiev_a.data.model;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class Event extends Model<Long> {
    private String name;
    private String description;
    private LocalDate date;

    public Event(Long id, String name, String description, LocalDate date) {
        super(id);
        this.name = name;
        this.description = description;
        this.date = date;
    }
}
