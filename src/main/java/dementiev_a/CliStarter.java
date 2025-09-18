package dementiev_a;

import dementiev_a.command.Command;
import dementiev_a.command.ExitCommand;
import dementiev_a.command.GetAllEventsCommand;

import java.util.*;

public class CliStarter implements Starter {
    private final Map<Integer, Command> commandsMap;

    public CliStarter() {
        commandsMap = new HashMap<>();
        List<Command> commandsList = List.of(
                new GetAllEventsCommand(),
                new ExitCommand()
        );
        for (int i = 0; i < commandsList.size(); i++) {
            commandsMap.put(i + 1, commandsList.get(i));
        }
    }

    @Override
    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            commandsMap.forEach((number, command) -> {
                System.out.printf("%d) %s\n", number, command.getName());
            });
            try {
                Integer commandNumber = scanner.nextInt();
                if (!commandsMap.containsKey(commandNumber)) {
                    System.out.println("Нет такой команды");
                    continue;
                }
                commandsMap.get(commandNumber).execute();
            } catch (InputMismatchException e) {
                System.out.println("Неверный формат ввода");
            }
        }
    }
}
