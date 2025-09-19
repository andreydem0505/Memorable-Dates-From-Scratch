package dementiev_a;

import dementiev_a.command.*;
import dementiev_a.io.IO;

import java.util.*;

public class CliRunner implements Runner {
    private static final List<Command> commandsList = List.of(
            new GetAllEventsCommand(),
            new AddEventCommand(),
            new GetEventCelebrationsCommand(),
            new ExitCommand()
    );

    private final Map<Integer, Command> commandsMap;

    public CliRunner() {
        commandsMap = new HashMap<>();
        for (int i = 0; i < commandsList.size(); i++) {
            commandsMap.put(i + 1, commandsList.get(i));
        }
    }

    @Override
    public void run() {
        while (true) {
            printAllCommands();
            Integer command = chooseCommand();
            if (command == null) {
                continue;
            }
            commandsMap.get(command).execute();
        }
    }

    private void printAllCommands() {
        commandsMap.forEach((number, command) -> {
            IO.print("%d) %s".formatted(number, command.getName()));
        });
    }

    private Integer chooseCommand() {
        try {
            Integer input = Integer.parseInt(IO.readLine("Выберите команду:"));
            if (!commandsMap.containsKey(input)) {
                IO.printError("Нет такой команды");
                return null;
            }
            return input;
        } catch (NumberFormatException e) {
            IO.printError("Неверный формат ввода");
        }
        return null;
    }
}
