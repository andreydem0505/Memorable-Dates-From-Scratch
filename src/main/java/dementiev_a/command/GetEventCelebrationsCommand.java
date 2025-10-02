package dementiev_a.command;

import dementiev_a.data.model.Celebration;
import dementiev_a.exception.NoEntityException;
import dementiev_a.io.IO;
import dementiev_a.service.EventService;
import dementiev_a.utils.DateUtils;

import java.util.Set;

public class GetEventCelebrationsCommand implements Command {
    @Override
    public String getName() {
        return "Get all event celebrations";
    }

    @Override
    public void execute() {
        long eventId;
        Set<Celebration> celebrations;
        try {
            eventId = Long.parseLong(IO.readLine("Input event ID:"));
            celebrations = EventService.getInstance().getCelebrationsByEventId(eventId);
        } catch (NumberFormatException e) {
            IO.printError("Event ID should be numeric");
            return;
        } catch (NoEntityException e) {
            IO.printError(e.getMessage());
            return;
        }
        if (celebrations.isEmpty()) {
            IO.print("No celebrations for event with ID=" + eventId);
            return;
        }
        celebrations.forEach(celebration -> {
            IO.print("%d. %s (%s) - %s; place: %s".formatted(
                    celebration.getId(),
                    celebration.getName(),
                    DateUtils.formatter.format(celebration.getDate()),
                    celebration.getDescription(),
                    celebration.getPlace()
            ));
        });
    }
}
