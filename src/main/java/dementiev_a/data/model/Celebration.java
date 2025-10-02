package dementiev_a.data.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Celebration extends Model<Long> {
    private long eventId;
    private String name;
    private String description;
    private LocalDate date;
    private String place;

    public Celebration(long eventId, String name, String description, LocalDate date, String place) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.date = date;
        this.place = place;
    }
}
