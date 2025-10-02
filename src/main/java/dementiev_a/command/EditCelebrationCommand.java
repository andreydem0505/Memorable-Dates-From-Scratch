package dementiev_a.command;

import dementiev_a.data.model.Celebration;
import dementiev_a.exception.NoEntityException;
import dementiev_a.io.IO;
import dementiev_a.service.CelebrationService;
import dementiev_a.utils.DateUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class EditCelebrationCommand implements Command {
    @Override
    public String getName() {
        return "Edit celebration";
    }

    @Override
    public void execute() {
        try {
            long celebrationId = Long.parseLong(IO.readLine("Input celebration ID:"));
            Celebration celebration = CelebrationService.getInstance().getCelebrationById(celebrationId);
            String name = IO.readLine("Input celebration title (or skip if you don't want it to change):");
            String description = IO.readLine("Input description (or skip if you don't want it to change):");
            String dateString = IO.readLine("Input date in format of 12.05.2007 (or skip if you don't want it to change):");
            String place = IO.readLine("Input place (or skip if you don't want it to change):");
            CelebrationService.getInstance().editCelebration(
                    celebrationId,
                    name.isBlank() ? celebration.getName() : name,
                    description.isBlank() ? celebration.getDescription() : description,
                    dateString.isBlank() ? celebration.getDate() : LocalDate.parse(
                            dateString,
                            DateUtils.formatter
                    ),
                    place.isBlank() ? celebration.getPlace() : place
            );
            IO.print("Celebration was successfully edited");
        } catch (NumberFormatException e) {
            IO.printError("Wrong ID format");
        } catch (NoEntityException e) {
            IO.printError(e.getMessage());
        } catch (DateTimeParseException e) {
            IO.printError("Wrong date format");
        }
    }
}
