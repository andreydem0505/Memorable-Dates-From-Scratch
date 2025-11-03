<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><%= request.getAttribute("id") != null ? "Edit event" : "Add event" %></title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css">
</head>
<body>
<div class="container">
    <h1><%= request.getAttribute("id") != null ? "✏️ Edit event" : "➕ Add event" %></h1>

    <% String error = (String) request.getAttribute("error"); %>
    <% if (error != null) { %>
        <div class="error"><%= error %></div>
    <% } %>

    <%
        String id = (String) request.getAttribute("id");
        String name = (String) request.getAttribute("name");
        String description = (String) request.getAttribute("description");
        String date = (String) request.getAttribute("date");
    %>

    <form action="<%= request.getContextPath() %>/events" method="post">
        <% if (id != null) { %>
            <input type="hidden" name="id" value="<%= id %>">
        <% } %>

        <div class="form-row">
            <label for="name">Name</label>
            <input id="name" name="name" type="text" required placeholder="My Birthday" value="<%= name != null ? name : "" %>">
        </div>

        <div class="form-row">
            <label for="date">Date (dd.MM.yyyy)</label>
            <input id="date" name="date" type="text" required placeholder="12.05.2007" value="<%= date != null ? date : "" %>">
        </div>

        <div class="form-row">
            <label for="description">Description</label>
            <textarea id="description" name="description" rows="5" placeholder="Short description..."><%= description != null ? description : "" %></textarea>
        </div>

        <div class="actions">
            <button type="submit" class="btn btn-primary"><%= id != null ? "Save" : "Create" %></button>
            <a href="<%= request.getContextPath() %>/events" class="btn btn-secondary">Cancel</a>
        </div>
    </form>
</div>
</body>
</html>
