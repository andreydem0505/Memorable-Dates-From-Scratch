package dementiev_a.command;

import dementiev_a.data.model.Event;
import dementiev_a.io.IO;
import dementiev_a.service.EventService;
import dementiev_a.utils.DateUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AddEventCommand extends Command {

    @Override
    public String getName() {
        return "Добавить памятную дату";
    }

    @Override
    public void execute() {
        String name = IO.readLine("Введите название памятной даты:");
        String description = IO.readLine("Введите описание памятной даты:");
        try {
            LocalDate date = LocalDate.parse(
                    IO.readLine("Введите дату (в формате 12.05.2007):"),
                    DateUtils.formatter
            );
            EventService.getInstance().addEvent(new Event(name, description, date));
            IO.print("Памятная дата успешно добавлена");
        } catch (DateTimeParseException e) {
            IO.printError("Неверный формат даты");
        }
    }
}
