package dementiev_a.command;

import dementiev_a.exception.NoEntityException;
import dementiev_a.io.IO;
import dementiev_a.service.CelebrationService;

public class DeleteCelebrationCommand implements Command {
    @Override
    public String getName() {
        return "Delete celebration";
    }

    @Override
    public void execute() {
        try {
            long eventId = Long.parseLong(IO.readLine("Input celebration ID:"));
            CelebrationService.getInstance().deleteCelebrationById(eventId);
            IO.print("Celebration was successfully deleted");
        } catch (NumberFormatException e) {
            IO.printError("Wrong ID format");
        } catch (NoEntityException e) {
            IO.printError(e.getMessage());
        }
    }
}
