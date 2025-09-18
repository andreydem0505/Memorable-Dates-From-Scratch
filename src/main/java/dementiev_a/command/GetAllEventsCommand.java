package dementiev_a.command;

import dementiev_a.data.model.Event;
import dementiev_a.service.EventService;
import dementiev_a.utils.DateUtils;

import java.util.List;

public class GetAllEventsCommand extends Command {

    @Override
    public String getName() {
        return "Получить все памятные даты";
    }

    @Override
    public void execute() {
        List<Event> events = EventService.getInstance().getAllEvents();
        if (events.isEmpty()) {
            System.out.println("Нет памятных дат");
            return;
        }
        events.forEach(event -> {
            System.out.printf(
                    "id=%d %s (%s)\n",
                    event.getId(),
                    event.getName(),
                    DateUtils.formatter.format(event.getDate())
            );
        });
    }
}
