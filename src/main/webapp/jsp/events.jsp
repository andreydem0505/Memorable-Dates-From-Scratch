<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="dementiev_a.data.model.Event" %>
<%@ page import="dementiev_a.utils.DateUtils" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Memorable Events</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css">
</head>
<body>
<div class="container">
    <div class="top-actions">
        <a class="btn btn-primary" href="<%= request.getContextPath() %>/events?action=add">➕ Add new event</a>
    </div>
    <h1>📅 Memorable Events</h1>
    <%
        List<Event> events = (List<Event>) request.getAttribute("events");
        if (events == null || events.isEmpty()) {
        %>
        <div class="no-events">
            <p>📭 No events yet</p>
        </div>
        <%
        } else {
            %>
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Date</th>
                    <th>Marks</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <%
                    for (Event event : events) {
                    %>
                    <tr>
                        <td><%= event.getId() %></td>
                        <td><strong><%= event.getName() != null ? event.getName() : "—" %></strong></td>
                        <td class="event-description" title="<%= event.getDescription() != null ? event.getDescription() : "" %>">
                            <%= event.getDescription() != null ? event.getDescription() : "—" %>
                        </td>
                        <td><%= event.getDate() != null ? event.getDate().format(DateUtils.formatter) : "—" %></td>
                        <td><%= event.getCelebrationIds().size() %></td>
                        <td>
                            <div>
                                <a class="info" href="<%= request.getContextPath() %>/events?action=view&id=<%= event.getId() %>">
                                    🔍 See more
                                </a>
                            </div>
                            <div>
                                <a class="info" href="<%= request.getContextPath() %>/events?action=edit&id=<%= event.getId() %>">
                                    ✏️ Edit
                                </a>
                            </div>
                            <div>
                                <a class="danger" href="<%= request.getContextPath() %>/events?action=delete&id=<%= event.getId() %>" onclick="return confirm('Are you sure you want to delete this event?');">
                                    🗑️ Delete
                                </a>
                            </div>
                        </td>
                    </tr>
                    <%
                    }
                %>
                </tbody>
            </table>
            <%
        }
    %>
</div>
</body>
</html>
