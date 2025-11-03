package dementiev_a.servlets;

import dementiev_a.data.manager.DatabaseManager;
import dementiev_a.data.manager.PostgresManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;

public class Servlet extends HttpServlet {
    private DatabaseManager databaseManager;

    @Override
    public void init() throws ServletException {
        super.init();
        databaseManager = PostgresManager.getInstance();
    }

    @Override
    public void destroy() {
        super.destroy();
        databaseManager.closeConnection();
    }
}
