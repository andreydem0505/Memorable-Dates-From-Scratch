package dementiev_a.command;

public class ExitCommand extends Command {
    @Override
    public String getName() {
        return "Выйти";
    }

    @Override
    public void execute() {
        System.exit(0);
    }
}
