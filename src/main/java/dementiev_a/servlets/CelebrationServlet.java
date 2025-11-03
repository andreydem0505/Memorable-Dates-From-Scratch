package dementiev_a.servlets;

import dementiev_a.data.model.Celebration;
import dementiev_a.exception.NoEntityException;
import dementiev_a.service.CelebrationService;
import dementiev_a.utils.DateUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@WebServlet("/celebrations")
public class CelebrationServlet extends Servlet {
    private CelebrationService celebrationService;

    @Override
    public void init() throws ServletException {
        super.init();
        celebrationService = CelebrationService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String id = req.getParameter("id");
        String eventId = req.getParameter("eventId");

        if ("view".equals(action) && id != null) {
            showCelebrationDetails(req, resp, Long.parseLong(id));
        } else if ("add".equals(action) && eventId != null) {
            addCelebration(req, resp, eventId);
        } else if ("edit".equals(action) && id != null) {
            editCelebration(req, resp, Long.parseLong(id));
        } else if ("delete".equals(action) && id != null && eventId != null) {
            deleteCelebration(req, resp, Long.parseLong(id), Long.parseLong(eventId));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String eventIdStr = req.getParameter("eventId");
        String idStr = req.getParameter("id");
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        String dateStr = req.getParameter("date");
        String place = req.getParameter("place");

        req.setAttribute("eventId", eventIdStr);
        req.setAttribute("id", idStr);
        req.setAttribute("name", name);
        req.setAttribute("description", description);
        req.setAttribute("date", dateStr);
        req.setAttribute("place", place);

        try {
            LocalDate date = LocalDate.parse(dateStr, DateUtils.formatter);

            if (idStr == null || idStr.isBlank()) {
                // Create
                long eventId = Long.parseLong(eventIdStr);
                celebrationService.addCelebration(eventId, name, description, date, place);
                resp.sendRedirect(req.getContextPath() + "/?action=view&id=" + eventId);
            } else {
                // Update
                long id = Long.parseLong(idStr);
                celebrationService.editCelebration(id, name, description, date, place);
                resp.sendRedirect(req.getContextPath() + "/celebrations?action=view&id=" + id);
            }
            return;

        } catch (DateTimeParseException e) {
            req.setAttribute("error", "Wrong date format: use dd.MM.yyyy");
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Incorrect ID");
        } catch (Exception e) {
            req.setAttribute("error", "Error while saving celebration: " + e.getMessage());
        }
        req.getRequestDispatcher("/jsp/upsert-celebration.jsp").forward(req, resp);
    }

    private void addCelebration(HttpServletRequest req, HttpServletResponse resp, String eventId)
            throws IOException, ServletException {
        req.setAttribute("eventId", eventId);
        req.getRequestDispatcher("/jsp/upsert-celebration.jsp").forward(req, resp);
    }

    private void editCelebration(HttpServletRequest req, HttpServletResponse resp, Long id)
            throws IOException, ServletException {
        Celebration celebration = celebrationService.getCelebrationById(id);
        if (celebration == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Celebration not found");
            return;
        }
        req.setAttribute("eventId", String.valueOf(celebration.getEventId()));
        req.setAttribute("id", celebration.getId().toString());
        req.setAttribute("name", celebration.getName());
        req.setAttribute("description", celebration.getDescription());
        req.setAttribute("date", celebration.getDate().format(DateUtils.formatter));
        req.setAttribute("place", celebration.getPlace());
        req.getRequestDispatcher("/jsp/upsert-celebration.jsp").forward(req, resp);
    }

    private void showCelebrationDetails(HttpServletRequest req, HttpServletResponse resp, long celebrationId)
            throws ServletException, IOException {
        try {
            Celebration celebration = celebrationService.getCelebrationById(celebrationId);

            req.setAttribute("celebration", celebration);
            req.getRequestDispatcher("/jsp/celebration-details.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect celebration ID");
        } catch (NoEntityException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

    private void deleteCelebration(HttpServletRequest req, HttpServletResponse resp, long id, long eventId)
            throws IOException {
        try {
            celebrationService.deleteCelebrationById(id);
            resp.sendRedirect(req.getContextPath() + "/?action=view&id=" + eventId);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect celebration ID");
        }
    }
}
