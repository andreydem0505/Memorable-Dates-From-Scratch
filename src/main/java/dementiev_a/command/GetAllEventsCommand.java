package dementiev_a.command;

import dementiev_a.data.model.Event;
import dementiev_a.io.IO;
import dementiev_a.service.EventService;
import dementiev_a.utils.DateUtils;

import java.util.List;

public class GetAllEventsCommand implements Command {

    @Override
    public String getName() {
        return "Get all events";
    }

    @Override
    public void execute() {
        List<Event> events = EventService.getInstance().getAllEvents();
        if (events.isEmpty()) {
            IO.print("No events found");
            return;
        }
        events.forEach(event -> {
            IO.print("%d. %s (%s) - %s".formatted(
                    event.getId(),
                    event.getName(),
                    DateUtils.formatter.format(event.getDate()),
                    event.getDescription()
            ));
        });
    }
}
