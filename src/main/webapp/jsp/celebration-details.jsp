<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="dementiev_a.data.model.Celebration" %>
<%@ page import="dementiev_a.utils.DateUtils" %>
<%@ page import="dementiev_a.utils.StringUtils" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Celebration's details</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css">
</head>
<body>
<div class="container">
    <%
        Celebration celebration = (Celebration) request.getAttribute("celebration");
    %>
    <div class="top-actions">
        <a class="btn btn-primary" href="<%= request.getContextPath() %>/celebrations?action=edit&id=<%= celebration.getId() %>">✏️ Edit</a>
        <a class="btn btn-danger" onclick="return confirm('Are you sure you want to delete this celebration?');"
           href="<%= request.getContextPath() %>/celebrations?action=delete&id=<%= celebration.getId() %>&eventId=<%= celebration.getEventId() %>">🗑️ Delete</a>
    </div>
    <h1>📋 Celebration's details</h1>

    <div class="field">
        <div class="label">🆔 ID</div>
        <div class="value"><%= celebration.getId() %></div>
    </div>

    <div class="field">
        <div class="label">📌 Name</div>
        <div class="value"><%= !StringUtils.isBlank(celebration.getName()) ? celebration.getName() : "—" %></div>
    </div>

    <div class="field">
        <div class="label">📝 Description</div>
        <div class="value"><%= !StringUtils.isBlank(celebration.getDescription()) ? celebration.getDescription() : "—" %></div>
    </div>

    <div class="field">
        <div class="label">📅 Date</div>
        <div class="value"><%= celebration.getDate() != null ? celebration.getDate().format(DateUtils.formatter) : "—" %></div>
    </div>

    <div class="field">
        <div class="label">📍 Place</div>
        <div class="value"><%= !StringUtils.isBlank(celebration.getPlace()) ? celebration.getPlace() : "—" %></div>
    </div>

    <a href="<%= request.getContextPath() %>/?action=view&id=<%= celebration.getEventId() %>">
        ← Back to event
    </a>
</div>
</body>
</html>

