package dementiev_a.command;

import dementiev_a.data.model.Celebration;
import dementiev_a.exception.NoEntityException;
import dementiev_a.io.IO;
import dementiev_a.service.EventService;
import dementiev_a.utils.DateUtils;

import java.util.Set;

public class GetEventCelebrationsCommand extends Command {
    @Override
    public String getName() {
        return "Получить все отмечания заданной памятной даты";
    }

    @Override
    public void execute() {
        long eventId;
        try {
            eventId = Long.parseLong(IO.readLine("Введите ID памятной даты:"));
        } catch (NumberFormatException e) {
            IO.printError("ID памятной даты должен быть числом");
            return;
        }
        Set<Celebration> celebrations;
        try {
            celebrations = EventService.getInstance().getCelebrationsByEventId(eventId);
        } catch (NoEntityException e) {
            IO.printError(e.getMessage());
            return;
        }
        if (celebrations.isEmpty()) {
            IO.print("Нет отмечаний для памятной даты с ID=" + eventId);
            return;
        }
        celebrations.forEach(celebration -> {
            IO.print("%d) %s (%s) - %s, место: %s".formatted(
                    celebration.getId(),
                    celebration.getName(),
                    DateUtils.formatter.format(celebration.getDate()),
                    celebration.getDescription(),
                    celebration.getPlace()
            ));
        });
    }
}
