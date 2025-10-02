package dementiev_a.command;

import dementiev_a.data.model.Event;
import dementiev_a.io.IO;
import dementiev_a.service.EventService;
import dementiev_a.utils.DateUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AddEventCommand implements Command {

    @Override
    public String getName() {
        return "Add event";
    }

    @Override
    public void execute() {
        String name = IO.readLine("Input event title:");
        String description = IO.readLine("Input description:");
        try {
            LocalDate date = LocalDate.parse(
                    IO.readLine("Input date (in format of 12.05.2007):"),
                    DateUtils.formatter
            );
            EventService.getInstance().addEvent(new Event(name, description, date));
            IO.print("Event was successfully added");
        } catch (DateTimeParseException e) {
            IO.printError("Wrong date format");
        }
    }
}
