package dementiev_a.command;

import dementiev_a.exception.NoEntityException;
import dementiev_a.io.IO;
import dementiev_a.service.CelebrationService;
import dementiev_a.service.EventService;
import dementiev_a.utils.DateUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AddCelebrationCommand implements Command {
    @Override
    public String getName() {
        return "Add celebration to event";
    }

    @Override
    public void execute() {
        try {
            long eventId = Long.parseLong(IO.readLine("Input event ID:"));
            EventService.getInstance().getEventById(eventId);
            String name = IO.readLine("Input title:");
            String description = IO.readLine("Input description:");
            LocalDate date = LocalDate.parse(
                IO.readLine("Input date (in format of 12.05.2007):"),
                DateUtils.formatter
            );
            String place = IO.readLine("Input place:");
            CelebrationService.getInstance().addCelebration(eventId, name, description, date, place);
            IO.print("Celebration was successfully added");
        } catch (NumberFormatException e) {
            IO.printError("Wrong ID format");
        } catch (DateTimeParseException e) {
            IO.printError("Wrong date format");
        } catch (NoEntityException e) {
            IO.printError(e.getMessage());
        }
    }
}

