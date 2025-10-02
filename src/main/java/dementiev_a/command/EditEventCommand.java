package dementiev_a.command;

import dementiev_a.data.model.Event;
import dementiev_a.exception.NoEntityException;
import dementiev_a.io.IO;
import dementiev_a.service.EventService;
import dementiev_a.utils.DateUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class EditEventCommand implements Command {
    @Override
    public String getName() {
        return "Edit event";
    }

    @Override
    public void execute() {
        try {
            long eventId = Long.parseLong(IO.readLine("Input event ID:"));
            Event event = EventService.getInstance().getEventById(eventId);
            String name = IO.readLine("Input event title (or skip if you don't want it to change):");
            String description = IO.readLine("Input description (or skip if you don't want it to change):");
            String dateString = IO.readLine("Input date in format of 12.05.2007 (or skip if you don't want it to change):");
            EventService.getInstance().editEvent(
                    eventId,
                    name.isBlank() ? event.getName() : name,
                    description.isBlank() ? event.getDescription() : description,
                    dateString.isBlank() ? event.getDate() : LocalDate.parse(
                            dateString,
                            DateUtils.formatter
                    )
            );
            IO.print("Event was successfully edited");
        } catch (NumberFormatException e) {
            IO.printError("Wrong ID format");
        } catch (NoEntityException e) {
            IO.printError(e.getMessage());
        } catch (DateTimeParseException e) {
            IO.printError("Wrong date format");
        }
    }
}
