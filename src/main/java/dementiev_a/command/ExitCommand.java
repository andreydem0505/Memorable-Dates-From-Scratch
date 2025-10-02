package dementiev_a.command;

public class ExitCommand implements Command {
    @Override
    public String getName() {
        return "Exit";
    }

    @Override
    public void execute() {
        System.exit(0);
    }
}
