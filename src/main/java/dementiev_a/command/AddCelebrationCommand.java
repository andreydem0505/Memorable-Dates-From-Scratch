package dementiev_a.command;

import dementiev_a.exception.NoEntityException;
import dementiev_a.io.IO;
import dementiev_a.service.CelebrationService;
import dementiev_a.service.EventService;
import dementiev_a.utils.DateUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AddCelebrationCommand extends Command {
    @Override
    public String getName() {
        return "Добавить отмечание памятной даты";
    }

    @Override
    public void execute() {
        try {
            long eventId = Long.parseLong(IO.readLine("Введите id памятной даты:"));
            try {
                EventService.getInstance().getEventById(eventId);
            } catch (NoEntityException e) {
                IO.printError(e.getMessage());
                return;
            }
            String name = IO.readLine("Введите заголовок:");
            String description = IO.readLine("Введите описание отмечания:");
            LocalDate date = LocalDate.parse(
                IO.readLine("Введите дату отмечания (в формате 12.05.2007):"),
                DateUtils.formatter
            );
            String place = IO.readLine("Введите место отмечания:");
            CelebrationService.getInstance().addCelebration(eventId, name, description, date, place);
            IO.print("Отмечание успешно добавлено");
        } catch (NumberFormatException e) {
            IO.printError("Неверный формат id памятной даты");
        } catch (DateTimeParseException e) {
            IO.printError("Неверный формат даты");
        }
    }
}

