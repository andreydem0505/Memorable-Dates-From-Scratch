<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="dementiev_a.data.model.Event" %>
<%@ page import="dementiev_a.data.model.Celebration" %>
<%@ page import="java.util.List" %>
<%@ page import="dementiev_a.utils.DateUtils" %>
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
            <h1>📋 Event's details</h1>

            <div class="field">
                <div class="label">🆔 ID</div>
                <div class="value"><%= event.getId() %></div>
            </div>

            <div class="field">
                <div class="label">📌 Name</div>
                <div class="value"><%= event.getName() != null ? event.getName() : "—" %></div>
            </div>

            <div class="field">
                <div class="label">📝 Description</div>
                <div class="value"><%= event.getDescription() != null ? event.getDescription() : "—" %></div>
            </div>

            <div class="field">
                <div class="label">📅 Date</div>
                <div class="value"><%= event.getDate() != null ? event.getDate().format(DateUtils.formatter) : "—" %></div>
            </div>

            <div class="field">
                <div class="label">🎉 Marks</div>
                <div class="value">
                    <% if (celebrations != null && !celebrations.isEmpty()) { %>
                        <ul class="marks-list">
                            <% for (Celebration celebration : celebrations) { %>
                                <li>
                                    <strong><%= celebration.getName() %></strong>
                                    <% if (celebration.getDescription() != null && !celebration.getDescription().isEmpty()) { %>
                                        <br><small style="color: #666;"><%= celebration.getDescription() %></small>
                                    <% } %>
                                </li>
                            <% } %>
                        </ul>
                    <% } else { %>
                        No connected marks
                    <% } %>
                </div>
            </div>
        <%
            }
        %>
        <a href="<%= request.getContextPath() %>/events">
            ← Back to list
        </a>
    </div>
</body>
</html>

