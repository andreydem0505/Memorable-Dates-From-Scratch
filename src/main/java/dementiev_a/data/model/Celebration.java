package dementiev_a.data.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

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

    public Celebration(long id, long eventId, String name, String description, LocalDate date, String place) {
        this.setId(id);
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.date = date;
        this.place = place;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Celebration that)) return false;

        return Objects.equals(getId(), that.getId()) &&
                eventId == that.eventId &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(date, that.date) &&
                Objects.equals(place, that.place);
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(getId());
        result = 31 * result + Objects.hashCode(eventId);
        result = 31 * result + Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(description);
        result = 31 * result + Objects.hashCode(date);
        result = 31 * result + Objects.hashCode(place);
        return result;
    }
}
