package dementiev_a.command;

import dementiev_a.exception.NoEntityException;
import dementiev_a.io.IO;
import dementiev_a.service.EventService;

public class DeleteEventCommand implements Command {
    @Override
    public String getName() {
        return "Delete event";
    }

    @Override
    public void execute() {
        try {
            long eventId = Long.parseLong(IO.readLine("Input event ID:"));
            EventService.getInstance().deleteEventById(eventId);
            IO.print("Event was successfully deleted");
        } catch (NumberFormatException e) {
            IO.printError("Wrong ID format");
        } catch (NoEntityException e) {
            IO.printError(e.getMessage());
        }
    }
}
