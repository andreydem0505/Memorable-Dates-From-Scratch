<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="dementiev_a.data.model.Event" %>
<%@ page import="dementiev_a.data.model.Celebration" %>
<%@ page import="java.util.List" %>
<%@ page import="dementiev_a.utils.DateUtils" %>
<%@ page import="dementiev_a.utils.StringUtils" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Event's details</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css">
</head>
<body>
    <div class="container">
        <%
            Event event = (Event) request.getAttribute("event");
            if (event != null) {
                List<Celebration> celebrations = (List<Celebration>) request.getAttribute("celebrations");
        %>
            <div class="top-actions">
                <a class="btn btn-primary" href="<%= request.getContextPath() %>/celebrations?action=add&eventId=<%= event.getId() %>">➕ Add new celebration</a>
            </div>
            <h1>📋 Event's details</h1>

            <div class="field">
                <div class="label">🆔 ID</div>
                <div class="value"><%= event.getId() %></div>
            </div>

            <div class="field">
                <div class="label">📌 Name</div>
                <div class="value"><%= !StringUtils.isBlank(event.getName()) ? event.getName() : "—" %></div>
            </div>

            <div class="field">
                <div class="label">📝 Description</div>
                <div class="value"><%= !StringUtils.isBlank(event.getDescription()) ? event.getDescription() : "—" %></div>
            </div>

            <div class="field">
                <div class="label">📅 Date</div>
                <div class="value"><%= event.getDate() != null ? event.getDate().format(DateUtils.formatter) : "—" %></div>
            </div>

            <div class="field">
                <div class="label">🎉 Celebrations</div>
                <div class="value">
                    <% if (celebrations != null && !celebrations.isEmpty()) { %>
                        <ul class="celebrations-list">
                            <% for (Celebration celebration : celebrations) { %>
                                <li>
                                    <a class="info" href="<%= request.getContextPath() %>/celebrations?action=view&id=<%= celebration.getId() %>">
                                        <strong><%= celebration.getName() %></strong>
                                    </a>
                                    <% if (celebration.getDescription() != null && !celebration.getDescription().isEmpty()) { %>
                                        <br><small style="color: #666;"><%= celebration.getDescription() %></small>
                                    <% } %>
                                </li>
                            <% } %>
                        </ul>
                    <% } else { %>
                        No connected celebrations
                    <% } %>
                </div>
            </div>
        <%
            }
        %>
        <a href="<%= request.getContextPath() %>/">
            ← Back to list
        </a>
    </div>
</body>
</html>

