<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" type="text/css" href="forms.css">
        <title>Login Page</title>
    </head>
    <body>
        <div class="container">
            <h2>Login</h2>
            <% String errorMessage = (String) request.getAttribute("errorMessage");
           if (errorMessage != null && !errorMessage.isEmpty()) { %>
            <div class="custom-alert">
                <%= errorMessage %>
            </div>
            <% } %>

            <form action="LoginServlet" method="post">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" placeholder="user@gmail.com" required>

                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required>

                <button type="submit">Login</button>
            </form>

            <p>Forgot your password? <a href="Forgot-Password.jsp">Reset Password</a></p>
            <p>Create a new account? <a href="Registration.jsp">New</a></p>
        </div>
    </body>
</html>
