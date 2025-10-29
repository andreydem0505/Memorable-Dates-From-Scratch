package dementiev_a.command;

import dementiev_a.data.manager.DatabaseManager;
import dementiev_a.data.manager.PostgresManager;

public class ExitCommand implements Command {
    DatabaseManager databaseManager = PostgresManager.getInstance();

    @Override
    public String getName() {
        return "Exit";
    }

    @Override
    public void execute() {
        databaseManager.closeConnection();
        System.exit(0);
    }
}
