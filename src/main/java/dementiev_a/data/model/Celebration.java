package dementiev_a.data.model;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class Celebration extends Model<Long> {
    private long eventId;
    private String name;
    private String description;
    private LocalDate date;
    private String place;

    public Celebration(Long id, long eventId, String name, String description, LocalDate date, String place) {
        super(id);
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.date = date;
        this.place = place;
    }
}
