package dementiev_a.command;

import dementiev_a.io.IO;
import dementiev_a.service.EventService;
import dementiev_a.utils.DateUtils;
import dementiev_a.data.model.Event;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Set;

public class GetEventsByDateCommand extends Command {
    @Override
    public String getName() {
        return "Получить памятные даты, произошедшие в определенный день";
    }

    @Override
    public void execute() {
        String input = IO.readLine("Введите дату (в формате 12.05.2007):");
        try {
            LocalDate date = LocalDate.parse(input, DateUtils.formatter);
            Set<Event> events = EventService.getInstance().getEventsByDate(date);
            if (events.isEmpty()) {
                IO.print("Нет памятных дат в этот день");
                return;
            }
            IO.print("Памятные даты на " + date.format(DateUtils.formatter) + ":");
            events.forEach(event -> {
                IO.print(event.getId() + ". " + event.getName() + " - " + event.getDescription());
            });
        } catch (DateTimeParseException e) {
            IO.printError("Неверный формат даты");
        }
    }
}

