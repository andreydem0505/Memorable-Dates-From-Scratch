package dementiev_a.servlets;

import dementiev_a.data.model.Celebration;
import dementiev_a.data.model.Event;
import dementiev_a.exception.NoEntityException;
import dementiev_a.service.EventService;
import dementiev_a.utils.DateUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@WebServlet("/events")
public class EventServlet extends HttpServlet {
    private EventService eventService;

    @Override
    public void init() throws ServletException {
        super.init();
        eventService = EventService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String id = req.getParameter("id");

        if ("view".equals(action) && id != null) {
            showEventDetails(req, resp, Long.parseLong(id));
        } else if ("add".equals(action)) {
            addEvent(req, resp);
        } else if ("edit".equals(action) && id != null) {
            editEvent(req, resp, Long.parseLong(id));
        } else if ("delete".equals(action) && id != null) {
            deleteEvent(req, resp, Long.parseLong(id));
        } else {
            showAllEvents(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        String dateStr = req.getParameter("date");

        req.setAttribute("id", idStr);
        req.setAttribute("name", name);
        req.setAttribute("description", description);
        req.setAttribute("date", dateStr);

        try {
            LocalDate date = LocalDate.parse(dateStr, DateUtils.formatter);

            if (idStr == null || idStr.isBlank()) {
                // Create
                Event event = new Event(name, description, date);
                eventService.addEvent(event);
            } else {
                // Update
                eventService.editEvent(Long.parseLong(idStr), name, description, date);
            }

            resp.sendRedirect(req.getContextPath() + "/events");
            return;

        } catch (DateTimeParseException e) {
            req.setAttribute("error", "Wrong date format: use dd.MM.yyyy");
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Incorrect ID");
        } catch (Exception e) {
            req.setAttribute("error", "Error while saving event: " + e.getMessage());
        }
        req.getRequestDispatcher("/jsp/upsert-event.jsp").forward(req, resp);
    }

    private void addEvent(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        req.getRequestDispatcher("/jsp/upsert-event.jsp").forward(req, resp);
    }

    private void editEvent(HttpServletRequest req, HttpServletResponse resp, Long eventId) throws IOException, ServletException {
        Event event = eventService.getEventById(eventId);
        if (event == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Event not found");
            return;
        }
        req.setAttribute("id", event.getId().toString());
        req.setAttribute("name", event.getName());
        req.setAttribute("description", event.getDescription());
        req.setAttribute("date", event.getDate().format(DateUtils.formatter));
        req.getRequestDispatcher("/jsp/upsert-event.jsp").forward(req, resp);
    }

    private void showAllEvents(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Event> events = eventService.getAllEvents();
        req.setAttribute("events", events);
        req.getRequestDispatcher("/jsp/events.jsp").forward(req, resp);
    }

    private void showEventDetails(HttpServletRequest req, HttpServletResponse resp, long eventId)
            throws ServletException, IOException {
        try {
            Event event = eventService.getEventById(eventId);

            List<Celebration> celebrations = eventService.getCelebrationsByEventId(eventId);

            req.setAttribute("event", event);
            req.setAttribute("celebrations", celebrations);
            req.getRequestDispatcher("/jsp/event-details.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect event ID");
        } catch (NoEntityException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

    private void deleteEvent(HttpServletRequest req, HttpServletResponse resp, long eventId) throws IOException {
        try {
            eventService.deleteEventById(eventId);
            resp.sendRedirect(req.getContextPath() + "/events");
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect event ID");
        }
    }
}
